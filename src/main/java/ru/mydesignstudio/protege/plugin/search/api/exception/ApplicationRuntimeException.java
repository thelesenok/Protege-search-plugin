package ru.mydesignstudio.protege.plugin.search.api.exception;

/**
 * Created by abarmin on 12.03.17.
 */
public class ApplicationRuntimeException extends RuntimeException {
    public ApplicationRuntimeException() {
    }

    public ApplicationRuntimeException(String message) {
        super(message);
    }

    public ApplicationRuntimeException(Throwable cause) {
        super(cause);
    }

    public ApplicationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
