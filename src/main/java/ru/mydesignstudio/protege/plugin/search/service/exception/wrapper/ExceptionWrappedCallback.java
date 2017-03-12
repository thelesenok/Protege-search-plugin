package ru.mydesignstudio.protege.plugin.search.service.exception.wrapper;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 12.03.17.
 */
public interface ExceptionWrappedCallback<T> {
    T run() throws ApplicationException;
}
