package ru.mydesignstudio.protege.plugin.search.utils;

/**
 * Created by abarmin on 05.01.17.
 */
public interface Specification<ITEM> {
    boolean isSatisfied(ITEM item);
}
