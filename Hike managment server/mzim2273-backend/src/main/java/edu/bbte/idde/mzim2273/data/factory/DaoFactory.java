package edu.bbte.idde.mzim2273.data.factory;

import edu.bbte.idde.mzim2273.data.dao.HikeDAO;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public abstract class DaoFactory {
    private static final Logger logger = LoggerFactory.getLogger(DaoFactory.class);

    // Abstract method to retrieve HikeDAO
    public abstract HikeDAO getHikeDAO();

    // Static method to retrieve the DaoFactory instance
    public static DaoFactory getFactory() throws RepositoryException {
        return DaoFactoryHolder.INSTANCE; // Access the holder class
    }

    // Private static inner class to hold the Singleton instance
    private static final class DaoFactoryHolder {
        private static final DaoFactory INSTANCE;

        static {
            try {
                INSTANCE = createFactoryFromConfig();
            } catch (RepositoryException e) {
                throw new ExceptionInInitializerError(e); // Throwing error if initialization fails
            }
        }

        private static DaoFactory createFactoryFromConfig() throws RepositoryException {
            try (InputStream input =
                         Thread.currentThread().getContextClassLoader().getResourceAsStream("application.yml")) {
                if (input == null) {
                    throw new RepositoryException("Could not find application.yml file.");
                }

                // Parse the YAML file using SnakeYAML
                Yaml yaml = new Yaml();
                Map<String, Object> config = yaml.load(input);

                // Retrieve the active profile
                String activeProfile = (String) ((Map<String, Object>) config.get("profiles")).get("active");

                if ("jdbc".equalsIgnoreCase(activeProfile)) {
                    logger.info("Creating JdbcDaoFactory");
                    Map<String, Object> jdbcConfig = (Map<String, Object>) config.get("jdbc");
                    return new JdbcDaoFactory(jdbcConfig);
                } else if ("in-memory".equalsIgnoreCase(activeProfile)) {
                    logger.info("Creating InMemoryDaoFactory");
                    return new InMemoryDaoFactory();
                } else {
                    throw new RepositoryException("Invalid DAO type specified in the YAML configuration.");
                }
            } catch (IOException e) {
                throw new RepositoryException("Error loading YAML configuration file.", e);
            }
        }
    }
}
