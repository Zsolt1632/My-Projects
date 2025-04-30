import express from 'express';
import path from 'path';
import multer from 'multer';
import fs from 'fs';
import { v4 as uuidv4 } from 'uuid';
import process from 'process';

const dataDir = path.join(process.cwd(), 'public/data');
const app = express();

// Adatbázis szimulációja
let films = [];

// Betölti a 'data' mappából az adatokat
const loadFilms = () => {
  try {
    if (!fs.existsSync(dataDir)) {
      fs.mkdirSync(dataDir, { recursive: true });
    }

    const files = fs.readdirSync(dataDir);
    if (files.length === 0) {
      films = [];
    } else {
      films = files
        .filter((file) => file.endsWith('.json')) // nem JSON fileüok kihagyása
        .map((file) => {
          const filePath = path.join(dataDir, file);
          const data = fs.readFileSync(filePath, 'utf-8');
          return JSON.parse(data);
        });
    }
  } catch (error) {
    console.error('Error loading films:', error);
    films = [];
  }
};

loadFilms();

app.use(express.urlencoded({ extended: true }));
app.use(express.static('public'));
app.use(express.json());

const uploadPicture = multer({
  dest: 'public/data/',
  fileFilter(req, file, cb) {
    // Check if the file extension is either jpg, jpeg, or png
    const allowedExtensions = ['jpg', 'jpeg', 'png'];
    const fileExtension = file.originalname.split('.').pop().toLowerCase();
    const isValidExtension = allowedExtensions.includes(fileExtension);

    if (!isValidExtension) {
      return cb(new Error('Only JPG, JPEG, and PNG files are allowed!'), false);
    }

    return cb(null, true); // Add return statement
  },
});

// Új film hozzáadása
app.post('/addFilm', uploadPicture.single('coverImageID'), (req, res) => {
  const { title, releaseDate, description, genre } = req.body;
  const coverImageId = req.file.filename;

  if (!title || !releaseDate || !description || !genre || !coverImageId) {
    // If any required fields are missing, delete the uploaded file
    if (coverImageId) {
      fs.unlink(path.join(uploadPicture.storagePath, coverImageId), (err) => {
        if (err) {
          console.error('Error deleting file:', err);
        }
        console.log('File deleted successfully');
      });
    }
    return res.status(400).send('Hiányzó adat(ok)');
  }

  const film = {
    id: uuidv4(),
    title,
    releaseDate,
    description,
    genre,
    coverImageId,
  };

  const jsonFilePath = path.join(dataDir, `${film.id}.json`);
  fs.writeFileSync(jsonFilePath, JSON.stringify(film));
  films.push(film);
  return res.send(`Film sikeresen feltöltése a követekző ID-val: ${film.id}`);
});

// Visszajelzés adása filmre
app.post('/addReview', uploadPicture.none(), (req, res) => {
  const { filmId, rating, comment } = req.body;

  if (!filmId || !rating || !comment) {
    return res.status(400).send('Missing required fields');
  }

  if (parseInt(rating, 10) < 1 || parseInt(rating, 10) > 5) {
    return res.status(400).send('Invalid rating');
  }

  const foundFilm = films.find((film) => film.id === filmId);
  if (!foundFilm) {
    return res.status(404).send('Film not found in the database');
  }

  foundFilm.reviews = foundFilm.reviews || [];
  foundFilm.reviews.push({ rating, comment });

  const jsonFilePath = path.join(dataDir, `${filmId}.json`);
  fs.writeFileSync(jsonFilePath, JSON.stringify(foundFilm));

  return res.send('Review added successfully');
});

// Visszajelzés adása filmre
app.post('/search', (req, res) => {
  const { title, genre, minYear, maxYear } = req.body;
  let filteredFilms = films;
  if (title) {
    filteredFilms = filteredFilms.filter((film) => film.title.toLowerCase().includes(title.toLowerCase()));
  }
  if (genre) {
    filteredFilms = filteredFilms.filter((film) => film.genre.toLowerCase().includes(genre.toLowerCase()));
  }
  if (minYear) {
    filteredFilms = filteredFilms.filter((film) => parseInt(film.releaseDate, 10) >= minYear);
  }
  if (maxYear) {
    filteredFilms = filteredFilms.filter((film) => parseInt(film.releaseDate, 10) <= maxYear);
  }

  return res.json(filteredFilms);
});

app.listen(8080, () => {
  console.log('Server listening on port 8080...');
});
