package ru.mydesignstudio.protege.plugin.search.api.exception;

/**
 * Created by abarmin on 12.03.17.
 */
public class ApplicationException extends Exception {
    public ApplicationException() {
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
