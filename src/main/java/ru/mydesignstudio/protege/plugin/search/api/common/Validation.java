package ru.mydesignstudio.protege.plugin.search.api.common;

/**
 * Created by abarmin on 24.06.17.
 */
public class Validation {
    /**
     * Проверка на корректность условия
     * @param message - сообщение, которое будет выведено в случае истинности условия
     * @param value - что проверяем
     */
    public static final void assertFalse(String message, boolean value) {
        assertTrue(message, !value);
    }
    /**
     * Проверка на корректность условия
     * @param message - сообщение, которое будет выдано в случае ложности условия
     * @param value - что проверяем
     */
    public static final void assertTrue(String message, boolean value) {
        if (!value) {
            fail(message);
        }
    }

    /**
     * Проверка на существование значения
     * @param message - сообщение, которое будет выдано в случае отсутствия значения
     * @param value - что проверяем
     */
    public static final void assertNotNull(String message, Object value) {
        if (value == null) {
            fail(message);
        }
    }

    /**
     * Выбросить исключение с указанным сообщением
     * @param message - сообщение исключения
     */
    private static void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
