package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related;

import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import java.util.Arrays;

/**
 * Created by abarmin on 25.06.17.
 *
 * Типы информации о связаных классах
 */
public enum ConceptType {
    /**
     * Один взвешенный тип
     */
    WEIGHTED("weighted"),
    /**
     * Несколько взвешенных типов
     */
    WEIGHTED_SUM("weightedSum");

    private final String type;

    ConceptType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ConceptType getConceptType(String type) {
        return CollectionUtils.findFirst(Arrays.asList(ConceptType.values()), new Specification<ConceptType>() {
            @Override
            public boolean isSatisfied(ConceptType conceptType) {
                return StringUtils.equalsIgnoreCase(type, conceptType.getType());
            }
        });
    }
}
