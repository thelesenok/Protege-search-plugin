package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.xml;

import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Arrays;

/**
 * Created by abarmin on 26.06.17.
 *
 * Адаптер для конвертации значения из xml в {@link CustomLabelType}
 */
public class CustomLabelTypeAdapter extends XmlAdapter<String, CustomLabelType> {
    @Override
    public CustomLabelType unmarshal(String v) throws Exception {
        return CollectionUtils.findFirst(Arrays.asList(CustomLabelType.values()), new Specification<CustomLabelType>() {
            @Override
            public boolean isSatisfied(CustomLabelType customLabelType) {
                return StringUtils.equalsIgnoreCase(
                        v,
                        customLabelType.getType()
                );
            }
        });
    }

    @Override
    public String marshal(CustomLabelType v) throws Exception {
        return v.getType();
    }
}
