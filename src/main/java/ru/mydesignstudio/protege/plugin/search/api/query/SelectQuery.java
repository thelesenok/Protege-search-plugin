package ru.mydesignstudio.protege.plugin.search.api.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by abarmin on 04.01.17.
 */
public class SelectQuery implements QueryObject {
    private FromType from;
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
}
