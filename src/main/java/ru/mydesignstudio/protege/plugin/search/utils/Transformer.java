package ru.mydesignstudio.protege.plugin.search.utils;

/**
 * Created by abarmin on 04.01.17.
 */
public interface Transformer<SOURCE, DESTINATION> {
    DESTINATION transform(SOURCE item);
}
