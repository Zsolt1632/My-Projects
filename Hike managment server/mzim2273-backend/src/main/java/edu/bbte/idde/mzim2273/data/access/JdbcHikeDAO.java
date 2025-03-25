package edu.bbte.idde.mzim2273.data.access;

import edu.bbte.idde.mzim2273.business.service.exception.EntityNotFoundException;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.dao.HikeDAO;
import edu.bbte.idde.mzim2273.data.model.Hike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class JdbcHikeDAO implements HikeDAO {
    private static final Logger logger = LoggerFactory.getLogger(JdbcHikeDAO.class);
    private final ConnectionPooling connectionPooling;

    public JdbcHikeDAO(ConnectionPooling connectionPooling) {
        this.connectionPooling = connectionPooling;
    }

    @Override
    public void create(Hike hike) throws RepositoryException {
        String sql = "INSERT INTO Hike (name, startLocation, startDate, startTime, price, archived) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = connectionPooling.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, hike.getName());
            statement.setString(2, hike.getStartLocation());
            statement.setDate(3, Date.valueOf(hike.getStartDate()));
            statement.setTime(4, Time.valueOf(hike.getStartTime()));
            statement.setDouble(5, hike.getPrice());
            statement.setBoolean(6, false);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    hike.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating hike: {}", e.getMessage(), e);
            throw new RepositoryException("Error creating hike", e);
        }
    }

    @Override
    public Hike read(Long id, Boolean isArchived) throws RepositoryException, EntityNotFoundException {
        String sql = "SELECT * FROM Hike WHERE id = ? and archived = ?";
        try (Connection connection = connectionPooling.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.setBoolean(2, isArchived);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToHike(resultSet);
                } else {
                    throw new EntityNotFoundException("Hike with ID " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error reading hike with ID {}: {}", id, e.getMessage(), e);
            throw new RepositoryException("Error reading hike");
        }
    }

    @Override
    public void update(Hike hike, Boolean isArchived) throws RepositoryException, EntityNotFoundException {
        String sql =
                "UPDATE Hike SET name = ?, startLocation = ?, startDate = ?, startTime = ?, price = ?, archived = ? "
                        + "WHERE id = ? and archived = ?";
        try (Connection connection = connectionPooling.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, hike.getName());
            statement.setString(2, hike.getStartLocation());
            statement.setDate(3, Date.valueOf(hike.getStartDate()));
            statement.setTime(4, Time.valueOf(hike.getStartTime()));
            statement.setDouble(5, hike.getPrice());
            statement.setBoolean(6, hike.getArchived());
            statement.setLong(7, hike.getId());
            statement.setBoolean(8, isArchived);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new EntityNotFoundException("Hike with ID " + hike.getId() + " not found.");
            }
        } catch (SQLException e) {
            logger.error("Error updating hike with ID {}: {}", hike.getId(), e.getMessage(), e);
            throw new RepositoryException("Error updating hike", e);
        }
    }

    @Override
    public void delete(Long id, Boolean isArchived) throws RepositoryException, EntityNotFoundException {
        String sql = "DELETE FROM Hike WHERE id = ? and archived = ?";
        try (Connection connection = connectionPooling.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.setBoolean(2, isArchived);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new EntityNotFoundException("Hike with ID " + id + " not found.");
            }
        } catch (SQLException | RepositoryException e) {
            logger.error("Error deleting hike with ID {}: {}", id, e.getMessage(), e);
            throw new RepositoryException("Error deleting hike", e);
        }
    }

    @Override
    public Collection<Hike> getAllHikes(Boolean isArchived) throws RepositoryException {
        String sql = "SELECT * FROM Hike where archived = ?";
        Collection<Hike> hikes = new ArrayList<>();
        try (Connection connection = connectionPooling.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, isArchived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                hikes.add(mapRowToHike(resultSet));
            }
        } catch (SQLException | RepositoryException e) {
            logger.error("Error fetching all hikes: {}", e.getMessage(), e);
            throw new RepositoryException("Error fetching all hikes", e);
        }
        return hikes;
    }

    private Hike mapRowToHike(ResultSet resultSet) throws SQLException {
        Hike hike = new Hike();
        hike.setId(resultSet.getLong("id"));
        hike.setName(resultSet.getString("name"));
        hike.setStartLocation(resultSet.getString("startLocation"));
        hike.setStartDate(resultSet.getDate("startDate").toLocalDate());
        hike.setStartTime(resultSet.getTime("startTime").toLocalTime());
        hike.setPrice(resultSet.getDouble("price"));
        hike.setArchived(resultSet.getBoolean("archived"));
        return hike;
    }
}
