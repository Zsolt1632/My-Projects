package edu.bbte.idde.mzim2273.presentation.buttons.exception;

public class HikeCreationException extends RuntimeException {
    public HikeCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HikeCreationException(String message) {
        super(message);
    }
}
