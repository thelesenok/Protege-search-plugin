package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by abarmin on 25.06.17.
 *
 * Адаптер для преобразования строк в ConceptType-ы
 */
public class ConceptTypeAdapter extends XmlAdapter<String, ConceptType> {
    @Override
    public ConceptType unmarshal(String value) throws Exception {
        return ConceptType.getConceptType(value);
    }

    @Override
    public String marshal(ConceptType v) throws Exception {
        return v.getType();
    }
}
