package ru.mydesignstudio.protege.plugin.search.api.query;

import java.util.ArrayList;
import java.util.Collection;
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
    /**
     * Какие поля должны попасть в результаты (кроме полей,
     * которые уже есть в WherePart)
     * // TODO: 13.05.17 объединить эти поля
     */
    private final Collection<SelectField> fields = new ArrayList<>();

    public FromType getFrom() {
        return from;
    }

    public void setFrom(FromType from) {
        this.from = from;
    }

    public List<WherePart> getWhereParts() {
        return Collections.unmodifiableList(whereParts);
    }

    /**
     * Удалить все условия
     */
    public void emptyWhereParts() {
        whereParts.clear();
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

    public Collection<SelectField> getSelectFields() {
        return Collections.unmodifiableCollection(fields);
    }

    /**
     * Добавить поле в перечень отбираемых
     * @param field - поле
     */
    public void addSelectField(SelectField field) {
        fields.add(field);
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
        for (SelectField field : getSelectFields()) {
            final SelectField clonedField = field.clone();
            query.addSelectField(clonedField);
        }
        return query;
    }
}
