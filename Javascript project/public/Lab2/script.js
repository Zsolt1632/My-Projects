const colors = ['piros', 'kék', 'zöld', 'sárga', 'narancs', 'lila'];

const canvas = document.getElementById('canvas');
const ctx = canvas.getContext('2d');

const guessesHistory = [];

function setMessage(message) {
  if (message.includes('Tippek')) {
    const currentGuess = message.replace('Tippek: ', '');
    const previousGuesses = guessesHistory.map((guess) => guess);
    document.getElementById('message').innerText = `Tippek: ${currentGuess}\n\nElozo tippek:\n${previousGuesses.join(
      '\n',
    )}`;
  } else {
    document.getElementById('message').innerText = message;
  }
}

function fillCanvas(correctPositions, correctColors) {
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  const squareSize = 20;
  const padding = 5;

  ctx.fillStyle = 'white';
  for (let i = 0; i < correctPositions; i++) {
    ctx.fillRect(padding + i * (squareSize + padding), padding, squareSize, squareSize);
  }

  ctx.fillStyle = 'black';
  for (let i = correctPositions; i < correctPositions + correctColors; i++) {
    ctx.fillRect(padding + i * (squareSize + padding), padding, squareSize, squareSize);
  }
}

let randomColors = [];

function generateRandomColors(numColors) {
  while (randomColors.length < 4) {
    const randomIndex = Math.floor(Math.random() * numColors);
    const color = colors[randomIndex];
    if (!randomColors.includes(color)) {
      randomColors.push(color);
    }
  }
  return randomColors;
}

randomColors = generateRandomColors(6);

let guesses = 0;

const button = document.getElementById('myButton');

function evaluateGuess(selectedColors) {
  let correctPositions = 0;
  let correctColors = 0;

  for (let i = 0; i < 4; i++) {
    if (selectedColors[i] === randomColors[i]) {
      correctPositions++;
    } else if (randomColors.includes(selectedColors[i])) {
      correctColors++;
    }
  }

  let message;
  if (correctPositions === 4) {
    let playerAge1 = document.getElementById('player-age');
    playerAge1 = parseInt(playerAge1.innerText, 10);
    if (playerAge1 < 18) {
      message = 'Gratulálok, nyertél!😊';
    } else {
      message = 'Nyertel, gratula!🍻';
    }
    button.disabled = true;
  } else {
    message = `Helyes színek és helyes pozíciók: ${correctPositions}, helyes színek, de helytelen pozíciók: ${correctColors}, helytelen szinek: ${
      4 - correctPositions - correctColors
    }\nTippek: ${selectedColors.join(' ')}`;
  }

  fillCanvas(correctPositions, correctColors);

  return message;
}

button.addEventListener('click', () => {
  const playerName = document.getElementById('player-name').value.trim();
  const playerAge = document.getElementById('player-age').value.trim();

  if (!playerName || !playerAge) {
    setMessage('Add meg a játékos nevét és életkorát!');
    return;
  }

  if (guesses >= 8) {
    let playerAge1 = document.getElementById('player-age');
    playerAge1 = parseInt(playerAge1.innerText, 10);
    if (playerAge1 < 18) {
      setMessage('Sajnálom, vesztettél. 😢');
    } else {
      setMessage('Vége!💀');
    }
    return;
  }

  guesses++;

  const selectedColors = [];
  const selectionElements = document.querySelectorAll('.color-selection');
  selectionElements.forEach((select) => {
    const selectedColor = select.value;
    if (selectedColor) {
      selectedColors.push(selectedColor);
    }
  });

  if (selectedColors.length !== 4) {
    setMessage('Pontosan négy színt válassz ki!');
    return;
  }

  if (!selectedColors.every((color) => colors.includes(color))) {
    setMessage('Csak a listából választhatsz színt!');
    return;
  }

  const result = evaluateGuess(selectedColors, randomColors);
  setMessage(result);

  guessesHistory.push(result);

  if (guesses === 8) {
    const selectionElement = document.querySelectorAll('.color-selection');
    selectionElement.forEach((select) => {
      select.disabled = true;
    });
  }
});
