package ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.renderer;

import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIClass;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIDataProperty;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIIndividual;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUILiteral;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIObjectProperty;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIProperty;

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
        icons.put(OWLUIClass.class, new ImageIcon(getIconUrl("PrimitiveClass.gif")));
        icons.put(OWLUIIndividual.class, new ImageIcon(getIconUrl("OWLIndividual.gif")));
        icons.put(OWLUILiteral.class, new ImageIcon(getIconUrl("OWLDatatypeProperty.gif")));
        icons.put(OWLUIProperty.class, new ImageIcon(getIconUrl("OWLObjectProperty.gif")));
        icons.put(OWLUIDataProperty.class, new ImageIcon(getIconUrl("OWLDatatypeProperty.gif")));
        icons.put(OWLUIObjectProperty.class, new ImageIcon(getIconUrl("OWLObjectProperty.gif")));
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
