package ru.mydesignstudio.protege.plugin.search.utils;

/**
 * Created by abarmin on 04.01.17.
 */
public class StringUtils {
    public static final boolean equalsIgnoreCase(String first, String second) {
        return first.equalsIgnoreCase(second);
    }

    public static final String substringBefore(String source, String delimiter) {
        if (source.indexOf(delimiter) > -1) {
            return source.substring(
                    0,
                    source.indexOf(delimiter)
            );
        }
        return new String();
    }

    public static final String substringAfter(String source, String delimiter) {
        if (source.indexOf(delimiter) > -1) {
            return source.substring(
                    source.indexOf(
                            delimiter
                    ) + 1
            );
        }
        return new String();
    }
}
