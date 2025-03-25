package edu.bbte.idde.mzim2273.data.factory;

import edu.bbte.idde.mzim2273.data.dao.HikeDAO;
import edu.bbte.idde.mzim2273.data.access.ConnectionPooling;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public final class JdbcDaoFactory extends DaoFactory {
    private final ConnectionPooling connectionPooling;
    private final HikeDAO hikeDAO;

    JdbcDaoFactory(Map<String, Object> jdbcConfig) throws RepositoryException {
        super();
        try {
            this.connectionPooling = ConnectionPooling.getInstance(jdbcConfig);
            this.hikeDAO = createHikeDAO();
        } catch (RepositoryException e) {
            throw new RepositoryException("Error initializing JdbcDaoFactory", e);
        }
    }

    private HikeDAO createHikeDAO() throws RepositoryException {
        try {
            Properties properties = new Properties();
            try (InputStream input = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("dao-config.properties")) {
                if (input == null) {
                    throw new RepositoryException("Unable to find dao-config.properties");
                }
                properties.load(input);
            }

            String hikeDaoClassName = properties.getProperty("hikeDaoClassName");
            Class<?> clazz = Class.forName(hikeDaoClassName);
            return (HikeDAO) clazz.getConstructor(ConnectionPooling.class).newInstance(connectionPooling);
        } catch (IOException e) {
            throw new RepositoryException("Failed to load properties file", e);
        } catch (ReflectiveOperationException e) {
            throw new RepositoryException("Error creating HikeDAO", e);
        }
    }

    @Override
    public HikeDAO getHikeDAO() {
        return hikeDAO;
    }
}
