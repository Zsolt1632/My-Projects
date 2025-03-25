package edu.bbte.idde.mzim2273.data.access.exception;

public class RepositoryException extends Exception {
    public RepositoryException(String message, Exception e) {
        super(message, e);
    }

    public RepositoryException(Exception e) {
        super(e);
    }

    public RepositoryException(String noConnectionsInPool) {
        super(noConnectionsInPool);
    }
}
