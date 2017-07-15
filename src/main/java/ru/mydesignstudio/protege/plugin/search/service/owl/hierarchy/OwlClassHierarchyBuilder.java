package ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy;

import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 25.06.17.
 * Строит иерархию от указанного класса до вершины иерархии
 */
@Component
public class OwlClassHierarchyBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(OwlClassHierarchyBuilder.class);

    @Inject
    private OWLService owlService;

    /**
     * Построить иерархию от указанного класса до Thing
     * @param currentClass - от какого класса начинаем строить иерархию
     * @return - коллекция
     * @throws ApplicationException
     */
    public Collection<OWLClass> build(OWLClass currentClass) throws ApplicationException {
        final Collection<OWLClass> classes = new ArrayList<>();
        OWLClass parentClass = owlService.getParentClass(currentClass);
        while (parentClass != null) {
            if (classes.contains(parentClass)) {
                LOGGER.error("Cycle in classes hierarchy");
                return classes;
            }
            classes.add(parentClass);
            parentClass = owlService.getParentClass(parentClass);
        }
        return classes;
    }
}
