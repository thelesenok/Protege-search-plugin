package ru.mydesignstudio.protege.plugin.search.api.common;

/**
 * Created by abarmin on 24.06.17.
 */
public class Validation {
    /**
     * Проверка на корректность условия
     * @param message - сообщение, которое будет выдано в случае ложности условия
     * @param value - что проверяем
     */
    public static final void assertTrue(String message, boolean value) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }
}
