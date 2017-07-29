package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml.Degree;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml.FuzzyOWL2;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

/**
 * Created by abarmin on 29/07/2017.
 *
 * Factory for extracting object property values from XML strings from annotations
 */
@Component
public class ObjectPropertyWeightFactory implements PropertyWeightFactory {
    @Override
    public double build(String xmlString) throws ApplicationException {
        try {
            final JAXBContext context = JAXBContext.newInstance(FuzzyOWL2.class, Degree.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final FuzzyOWL2 value = (FuzzyOWL2) unmarshaller.unmarshal(new StringReader(xmlString));
            return value.getDegree().getValue();
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
}
