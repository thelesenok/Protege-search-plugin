package ru.mydesignstudio.protege.plugin.search.api.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by abarmin on 04.01.17.
 *
 * Объектное представление запроса
 */
public class SelectQuery implements QueryObject {
    /**
     * Какого типа результат
     */
    private FromType from;
    /**
     * По каким условиям отбираем
     */
    private final List<WherePart> whereParts = new ArrayList<>();

    public FromType getFrom() {
        return from;
    }

    public void setFrom(FromType from) {
        this.from = from;
    }

    public List<WherePart> getWhereParts() {
        return Collections.unmodifiableList(whereParts);
    }

    public void addWherePart(WherePart wherePart) {
        whereParts.add(wherePart);
    }

    public boolean hasWherePart(int index) {
        return (index >= 0) && (whereParts.size() > index);
    }

    public void removeWherePart(int index) {
        if (hasWherePart(index)) {
            whereParts.remove(index);
        }
    }

    @Override
    public SelectQuery clone() {
        final SelectQuery query = new SelectQuery();
        final FromType from = getFrom().clone();
        query.setFrom(from);
        for (WherePart wherePart : getWhereParts()) {
            final WherePart clonedWherePart = wherePart.clone();
            query.addWherePart(clonedWherePart);
        }
        return query;
    }
}
