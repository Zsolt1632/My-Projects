package edu.bbte.idde.mzim2273.data.factory;

import edu.bbte.idde.mzim2273.data.dao.HikeDAO;
import edu.bbte.idde.mzim2273.data.access.InMemoryHikeDAO;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryDaoFactory extends DaoFactory {
    private static HikeDAO hikeDaoInstance;
    private static final ReentrantLock lock = new ReentrantLock();

    @Override
    public HikeDAO getHikeDAO() {
        if (hikeDaoInstance == null) {
            lock.lock();  // Acquire the lock
            try {
                if (hikeDaoInstance == null) {
                    hikeDaoInstance = new InMemoryHikeDAO();
                }
            } finally {
                lock.unlock();  // Ensure lock is released even if an exception occurs
            }
        }
        return hikeDaoInstance;
    }
}
