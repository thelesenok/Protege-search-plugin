package ru.mydesignstudio.protege.plugin.search.service.exception.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;

/**
 * Created by abarmin on 12.03.17.
 *
 * Сервис оборачивания вызовов с проверяемыми исключения в вызовы
 * с непроверяемыми исключениями. Похоже на какой-то костыль.
 */
public class ExceptionWrapperService {
    /**
     * Вызвать метод обернув обработку исключений в непроверяемое
     * @param callback - колбек
     * @param <T>
     * @return - результат выполнения
     */
    public <T> T invokeWrapped(ExceptionWrappedCallback<T> callback) {
        try {
            return callback.run();
        } catch (ApplicationException e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    public <T> T invokeWrapped(Object caller, ExceptionWrappedCallback<T> callback) {
        try {
            return callback.run();
        } catch (ApplicationException e) {
            final Logger LOGGER = LoggerFactory.getLogger(caller.getClass());
            LOGGER.warn("Can't invoke callback {}", e);
            throw new ApplicationRuntimeException(e);
        }
    }
}
