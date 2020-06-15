package me.rkomarov.catalog.exception;

public class NotFoundException extends BusinessLogicException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
