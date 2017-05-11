package ru.mydesignstudio.protege.plugin.search.utils;

import java.util.Collection;

/**
 * Created by abarmin on 04.01.17.
 */
public class StringUtils {
    /**
     * Объединить коллекцию строк через разделитель
     * @param parts - коллекция строк
     * @param glue - разделитель
     * @return
     */
    public static final String join(Collection<String> parts, String glue) {
        final StringBuilder builder = new StringBuilder();
        //
        boolean isFirst = true;
        for (String part : parts) {
            if (!isFirst) {
                builder.append(glue);
            }
            isFirst = false;
            builder.append(part);
        }
        //
        return builder.toString();
    }

    /**
     * Повтроить символ указанное число раз
     * @param source - символ
     * @param times - сколько раз
     * @return
     */
    public static final String repeat(Character source, int times) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(source);
        }
        return builder.toString();
    }

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
