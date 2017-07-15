package ru.mydesignstudio.protege.plugin.search.api.common;

/**
 * Created by abarmin on 29.05.17.
 *
 * Объект - пара
 */
public class Pair<K, V> {
    private final K first;
    private final V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}
