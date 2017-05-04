package ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.renderer;

import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainClass;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainDataProperty;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainIndividual;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainLiteral;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainObjectProperty;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainProperty;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abarmin on 11.03.17.
 */
public class ComponentIconFactory {
    private static final Map<Class, Icon> icons = new HashMap<>();

    static {
        icons.put(OWLDomainClass.class, new ImageIcon(getIconUrl("PrimitiveClass.gif")));
        icons.put(OWLDomainIndividual.class, new ImageIcon(getIconUrl("OWLIndividual.gif")));
        icons.put(OWLDomainLiteral.class, new ImageIcon(getIconUrl("OWLDatatypeProperty.gif")));
        icons.put(OWLDomainProperty.class, new ImageIcon(getIconUrl("OWLObjectProperty.gif")));
        icons.put(OWLDomainDataProperty.class, new ImageIcon(getIconUrl("OWLDatatypeProperty.gif")));
        icons.put(OWLDomainObjectProperty.class, new ImageIcon(getIconUrl("OWLObjectProperty.gif")));
    }

    private static URL getIconUrl(String icon) {
        return ComponentIconFactory.class.getResource("/icons/" + icon);
    }

    public static final Icon getIcon(Object value) {
        if (value == null) {
            return null;
        }
        return icons.get(value.getClass());
    }
}
