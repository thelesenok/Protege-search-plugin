package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.xml.CustomLabel;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.xml.CustomLabelType;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.xml.Priority;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

/**
 * Created by abarmin on 26.06.17.
 *
 * Property weight extractor for data properties
 */
@Component
public class DataPropertyWeightFactory implements PropertyWeightFactory {
    /**
     * Размаршаллить xml строку и извлечь из нее значение веса атрибута
     * @param xmlString - xml строка
     * @return - вес атрибута
     * @throws ApplicationException
     */
    public double build(String xmlString) throws ApplicationException {
        try {
            final JAXBContext context = JAXBContext.newInstance(CustomLabel.class, Priority.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final CustomLabel customLabel = (CustomLabel) unmarshaller.unmarshal(new StringReader(xmlString));
            if (CustomLabelType.PRIORITY.equals(customLabel.getType())) {
                /**
                 * Это вес атрибута
                 */
                return customLabel.getPriority().getValue();
            } else {
                throw new ApplicationException(String.format(
                        "%s is not supported type",
                        customLabel.getType()
                ));
            }
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
}
