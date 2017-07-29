package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 29/07/2017.
 *
 * Property weight from XML strings extractor
 */
public interface PropertyWeightFactory {
    /**
     * Parse XML string and extract property weight from annotation xml
     * @param xmlString - xml property string
     * @return - property weight
     * @throws ApplicationException
     */
    double build(String xmlString) throws ApplicationException;
}
