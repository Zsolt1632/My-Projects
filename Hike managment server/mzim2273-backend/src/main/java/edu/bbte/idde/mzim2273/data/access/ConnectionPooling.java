package edu.bbte.idde.mzim2273.data.access;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public final class ConnectionPooling {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionPooling.class);
    private static ConnectionPooling instance;
    private final HikariDataSource dataSource;

    private ConnectionPooling(Map<String, Object> jdbcConfig) throws RepositoryException {
        LOG.info(jdbcConfig.get("url").toString());
        try {
            // Ensure that the JDBC driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Set up HikariCP configuration using the jdbcConfig map
            HikariConfig config = new HikariConfig();

            // Extract JDBC connection details from the jdbcConfig map
            String jdbcUrl = (String) jdbcConfig.get("url");
            String username = (String) jdbcConfig.get("username");
            String password = (String) jdbcConfig.get("password");
            int poolSize = Integer.parseInt(jdbcConfig.get("poolSize").toString());

            // Set HikariCP configuration values
            config.setJdbcUrl(jdbcUrl); // Set the JDBC URL from the configuration
            config.setUsername(username); // Set the DB username from the configuration
            config.setPassword(password); // Set the DB password from the configuration
            config.setMaximumPoolSize(poolSize); // Set the maximum pool size from the configuration

            // Initialize the HikariDataSource with the configuration
            this.dataSource = new HikariDataSource(config);

            LOG.info("Initialized HikariCP connection pool with size {}", poolSize);
        } catch (ClassNotFoundException e) {
            LOG.error("Connection pool could not be established", e);
            throw new RepositoryException("Connection pool could not be established", e);
        }
    }


    public static synchronized ConnectionPooling getInstance(Map<String, Object> jdbcConfig)
            throws RepositoryException {
        if (instance == null) {
            instance = new ConnectionPooling((Map<String, Object>) jdbcConfig.get("jdbc"));
        }
        return instance;
    }

    public Connection getConnection() throws RepositoryException {
        try {
            LOG.info("Getting connection from HikariCP pool");
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOG.error("Error getting connection from pool", e);
            throw new RepositoryException("Error getting connection from pool", e);
        }
    }

    public void close() {
        if (dataSource != null) {
            LOG.info("Shutting down HikariCP connection pool");
            dataSource.close();
        }
    }
}
