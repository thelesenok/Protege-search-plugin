package ru.mydesignstudio.protege.plugin.search.service.search.path;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.SetUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.Transformer;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Билдер путей для тестов
 *
 * Created by Aleksandr_Barmin on 6/30/2017.
 */
public class TestPathBuilder {
    private final Collection<VertexPath> vertexPaths = new ArrayList<>();

    protected TestPathBuilder(OWLService owlService) throws ApplicationException {
        /**
         * Получение свойств класса
         */
        Mockito.doAnswer(new Answer<Collection<OWLObjectProperty>>() {
            @Override
            public Collection<OWLObjectProperty> answer(InvocationOnMock invocation) throws Throwable {
                final OWLClass argument = invocation.getArgumentAt(0, OWLClass.class);
                /**
                 * Соберем все вершины, у которых from == argument
                 */
                final Collection<VertexPath> vertexes = CollectionUtils.filter(vertexPaths, new Specification<VertexPath>() {
                    @Override
                    public boolean isSatisfied(VertexPath vertexPath) {
                        return OWLUtils.equals(vertexPath.getFromClass(), argument);
                    }
                });
                /**
                 * Все доступные здесь свойства
                 */
                return CollectionUtils.map(vertexes, new Transformer<VertexPath, OWLObjectProperty>() {
                    @Override
                    public OWLObjectProperty transform(VertexPath item) {
                        return item.getProperty();
                    }
                });
            }
        }).when(owlService).getObjectProperties(Mockito.any(OWLClass.class));
        /**
         * Получение классов, которые соответствуют возможным значениям свойства
         */
        Mockito.doAnswer(new Answer<Collection<OWLPropertyRange>>(){
            @Override
            public Collection<OWLPropertyRange> answer(InvocationOnMock invocation) throws Throwable {
                final OWLObjectProperty property = invocation.getArgumentAt(0, OWLObjectProperty.class);
                /**
                 * Отберем все вершины, которые используют это свойство
                 */
                final Collection<VertexPath> vertexes = CollectionUtils.filter(vertexPaths, new Specification<VertexPath>() {
                    @Override
                    public boolean isSatisfied(VertexPath vertexPath) {
                        return OWLUtils.equals(
                                vertexPath.getProperty(),
                                property
                        );
                    }
                });
                /**
                 * Все классы, с которыми эти свойства связаны
                 */
                final Collection<OWLClass> relatedClasses = CollectionUtils.map(vertexes, new Transformer<VertexPath, OWLClass>() {
                    @Override
                    public OWLClass transform(VertexPath item) {
                        return item.getToPath();
                    }
                });
                /**
                 * Собираем range
                 */
                return Collections.singletonList(new OWLDummyPropertyRange(relatedClasses));
            }
        }).when(owlService).getPropertyRanges(Mockito.any(OWLObjectProperty.class));
    }

    /**
     * Билдер путей для тестов. Параллельно обучает мок сервиса правильным ответам
     * @param owlService- мок-объект сервиса
     * @return
     */
    public static final TestPathBuilder builder(OWLService owlService) throws ApplicationException {
        return new TestPathBuilder(owlService);
    }

    /**
     * Добавить вершину
     * @param vertexPath - вершина для добавления
     * @return
     */
    public TestPathBuilder addVertex(VertexPath vertexPath) {
        vertexPaths.add(vertexPath);
        return this;
    }

    private static class OWLDummyPropertyRange extends OWLObjectPropertyImpl implements OWLPropertyRange {
        private final Set<OWLClass> classes;

        public OWLDummyPropertyRange(Collection<OWLClass> classes) {
            this(IRI.create("dummy"), SetUtils.toSet(classes));
        }

        public OWLDummyPropertyRange(IRI iri, Set<OWLClass> classes) {
            super(iri);
            this.classes = classes;
        }

        @Override
        public Set<OWLClass> getClassesInSignature() {
            return classes;
        }
    }
}
