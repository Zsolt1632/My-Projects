package edu.bbte.idde.mzim2273;

import edu.bbte.idde.mzim2273.business.database.DatabaseInitializer;
import edu.bbte.idde.mzim2273.business.service.HikeServiceImpl;
import edu.bbte.idde.mzim2273.data.dao.HikeDAO;
import edu.bbte.idde.mzim2273.data.access.JdbcHikeDAO;
import edu.bbte.idde.mzim2273.data.access.exception.RepositoryException;
import edu.bbte.idde.mzim2273.data.factory.DaoFactory;
import edu.bbte.idde.mzim2273.presentation.HikeUI;

public class Main {
    public static void main(String[] args) throws RepositoryException {
        DaoFactory daoFactory;
        daoFactory = DaoFactory.getFactory();

        HikeDAO hikeDAO = daoFactory.getHikeDAO();
        if (hikeDAO instanceof JdbcHikeDAO) {
            String username = "username";
            String password = "password";
            // Initialize the database
            DatabaseInitializer databaseInitializer = new DatabaseInitializer(username, password);
            databaseInitializer.initializeDatabase();
        }
        // Service instance with DAO
        HikeServiceImpl hikeService = HikeServiceImpl.getInstance();

        // UI instance with Service
        HikeUI hikeUI = HikeUI.create(hikeService);

        // Display UI
        if (hikeUI != null) {
            hikeUI.display();
        }
    }
}
