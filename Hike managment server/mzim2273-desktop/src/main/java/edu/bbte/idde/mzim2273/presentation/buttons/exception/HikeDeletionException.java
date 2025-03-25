package edu.bbte.idde.mzim2273.presentation.buttons.exception;

public class HikeDeletionException extends RuntimeException {
    public HikeDeletionException(String message, Throwable cause) {
        super(message, cause);
    }

    public HikeDeletionException(String message) {
        super(message);
    }
}
