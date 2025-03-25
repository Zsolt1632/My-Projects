package edu.bbte.idde.mzim2273.business.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "hiking";

    private final String user;
    private final String password;

    // Constructor to accept credentials
    public DatabaseInitializer(String user, String password) {
        this.user = user;
        this.password = password;
    }

    // Method to initialize the database
    public void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, user, password);
             Statement statement = connection.createStatement()) {

            // Create the database if it doesn't exist
            String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            statement.executeUpdate(createDatabaseSQL);
            logger.info("Database created successfully (if it didn't exist)...");

            // Use the newly created database
            String useDatabaseSQL = "USE " + DB_NAME;
            statement.executeUpdate(useDatabaseSQL);

            // Create the table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Hike ("
                    + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "startLocation VARCHAR(255) NOT NULL, "
                    + "startDate DATE NOT NULL, "
                    + "startTime TIME NOT NULL, "
                    + "price DECIMAL(10, 2) NOT NULL"
                    + "archived BOOLEAN NOT NULL DEFAULT false )";
            statement.executeUpdate(createTableSQL);
            logger.info("Table created successfully (if it didn't exist)...");

        } catch (SQLException e) {
            logger.error("SQL error: {}", e.getMessage(), e);
        }
    }
}
