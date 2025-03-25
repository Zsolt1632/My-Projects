package edu.bbte.idde.mzim2273.business.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Service
@Profile("jdbc")
public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${jdbc.url}")
    private String dbUrl;

    @Value("${jdbc.database}")
    private String dbName;

    @Value("${jdbc.username}")
    private String user;

    @Value("${jdbc.password}")
    private String password;

    public DatabaseInitializer() {}

    @PostConstruct
    public void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(dbUrl, user, password);
             Statement statement = connection.createStatement()) {

            // Create the database if it doesn't exist (only for MySQL)
            if (dbUrl.contains("mysql")) {
                String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + dbName;
                statement.executeUpdate(createDatabaseSQL);
                logger.info("Database created successfully (if it didn't exist)...");

                // Use the newly created database
                String useDatabaseSQL = "USE " + dbName;
                statement.executeUpdate(useDatabaseSQL);
            }

            // Create the Hike table if it doesn't exist
            String createHikeTableSQL = "CREATE TABLE IF NOT EXISTS Hike ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "startLocation VARCHAR(255) NOT NULL, "
                    + "startDate DATE NOT NULL, "
                    + "startTime TIME NOT NULL, "
                    + "price DECIMAL(10, 2) NOT NULL"
                    + ")";
            statement.executeUpdate(createHikeTableSQL);
            logger.info("Hike table created successfully (if it didn't exist)...");

            // Create the Hiker table if it doesn't exist
            String createHikerTableSQL = "CREATE TABLE IF NOT EXISTS Hiker ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "age INT NOT NULL, "
                    + "email VARCHAR(255) NOT NULL UNIQUE"
                    + ")";
            statement.executeUpdate(createHikerTableSQL);
            logger.info("Hiker table created successfully (if it didn't exist)...");

            // Create the join table for the many-to-many relationship (hiker_hikes)
            String createJoinTableSQL = "CREATE TABLE IF NOT EXISTS hiker_hikes ("
                    + "hiker_id BIGINT, "
                    + "hike_id BIGINT, "
                    + "PRIMARY KEY (hiker_id, hike_id), "
                    + "FOREIGN KEY (hiker_id) REFERENCES Hiker(id), "
                    + "FOREIGN KEY (hike_id) REFERENCES Hike(id)"
                    + ")";
            statement.executeUpdate(createJoinTableSQL);
            logger.info("Join table 'hiker_hikes' created successfully (if it didn't exist)...");

        } catch (SQLException e) {
            logger.error("SQL error: {}", e.getMessage(), e);
        }
    }
}
