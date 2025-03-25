package edu.bbte.idde.mzim2273.data.access;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.dao.HikeDAO;
import edu.bbte.idde.mzim2273.data.model.Hike;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Profile("jdbc")
@Repository
public class JdbcHikeDAO implements HikeDAO {

    private static final Logger logger = LoggerFactory.getLogger(JdbcHikeDAO.class);
    private final JdbcTemplate jdbcTemplate;

    public JdbcHikeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Hike hike) throws RepositoryException {
        String sql = "INSERT INTO Hike (name, startLocation, startDate, startTime, price) VALUES (?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, hike.getName(), hike.getStartLocation(),
                    Date.valueOf(hike.getStartDate()), Time.valueOf(hike.getStartTime()), hike.getPrice());
        } catch (Exception e) {
            logger.error("Error creating hike: {}", e.getMessage(), e);
            throw new RepositoryException("Error creating hike", e);
        }
    }

    @Override
    public Hike read(Long id) throws RepositoryException, EntityNotFoundException {
        String sql = "SELECT * FROM Hike WHERE id = ?";
        try {
            List<Hike> hikes = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> mapRowToHike(rs));
            if (hikes.isEmpty()) {
                throw new EntityNotFoundException("Hike with ID " + id + " not found.");
            }
            return hikes.get(0);
        } catch (Exception e) {
            logger.error("Error reading hike with ID {}: {}", id, e.getMessage(), e);
            throw new RepositoryException("Error reading hike", e);
        }
    }

    @Override
    public void update(Hike hike) throws RepositoryException, EntityNotFoundException {
        String sql = "UPDATE Hike SET name = ?, startLocation = ?, startDate = ?, startTime = ?, price = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, hike.getName(), hike.getStartLocation(),
                    Date.valueOf(hike.getStartDate()), Time.valueOf(hike.getStartTime()), hike.getPrice(), hike.getId());
            if (rowsAffected == 0) {
                throw new EntityNotFoundException("Hike with ID " + hike.getId() + " not found.");
            }
        } catch (Exception e) {
            logger.error("Error updating hike with ID {}: {}", hike.getId(), e.getMessage(), e);
            throw new RepositoryException("Error updating hike", e);
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException, EntityNotFoundException {
        String sql = "DELETE FROM Hike WHERE id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            if (rowsAffected == 0) {
                throw new EntityNotFoundException("Hike with ID " + id + " not found.");
            }
        } catch (Exception e) {
            logger.error("Error deleting hike with ID {}: {}", id, e.getMessage(), e);
            throw new RepositoryException("Error deleting hike", e);
        }
    }

    @Override
    public List<Hike> findAll() throws RepositoryException {
        String sql = "SELECT * FROM Hike";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToHike(rs));
        } catch (Exception e) {
            logger.error("Error fetching all hikes: {}", e.getMessage(), e);
            throw new RepositoryException("Error fetching all hikes", e);
        }
    }

    @Override
    public Collection<Hike> getByName(String name) throws RepositoryException {
        return findAll().stream().filter(hike -> hike.getName() != null && hike.getName().contains(name)).toList();
    }

    @Override
    public Collection<Hike> filterByPrice(double price) throws RepositoryException {
        return findAll().stream().filter(hike -> hike.getPrice() <= price).toList();
    }

    private Hike mapRowToHike(java.sql.ResultSet rs) throws java.sql.SQLException {
        Hike hike = new Hike();
        hike.setId(rs.getLong("id"));
        hike.setName(rs.getString("name"));
        hike.setStartLocation(rs.getString("startLocation"));
        hike.setStartDate(rs.getDate("startDate").toLocalDate());
        hike.setStartTime(rs.getTime("startTime").toLocalTime());
        hike.setPrice(rs.getDouble("price"));
        return hike;
    }
}
