package edu.bbte.idde.mzim2273.presentation.buttons.exception;

public class HikeUpdateException extends RuntimeException {
    public HikeUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public HikeUpdateException(String message) {
        super(message);
    }
}
