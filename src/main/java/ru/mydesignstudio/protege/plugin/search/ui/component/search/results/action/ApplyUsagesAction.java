package ru.mydesignstudio.protege.plugin.search.ui.component.search.results.action;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.utils.Action;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplInteger;

import java.util.Collection;

/**
 * Created by abarmin on 04.06.17.
 *
 * Действие по увеличению значения свойства
 */
public class ApplyUsagesAction implements Action<Integer> {
    private final ResultSet resultSet;
    private final String propertyName;
    private final OWLService owlService;
    private final ExceptionWrapperService wrapperService;

    public ApplyUsagesAction(ResultSet resultSet, String propertyName) {
        this.resultSet = resultSet;
        this.propertyName = propertyName;
        this.owlService = InjectionUtils.getInstance(OWLService.class);
        this.wrapperService = InjectionUtils.getInstance(ExceptionWrapperService.class);
    }

    @Override
    public void run(Integer rowIndex) {
        /**
         * Определим запись по номеру строки
         */
        final IRI recordIri = (IRI) resultSet.getResult(rowIndex, resultSet.getColumnIndex(FieldConstants.OBJECT_IRI));
        final OWLIndividual record = wrapperService.invokeWrapped(new ExceptionWrappedCallback<OWLIndividual>() {
            @Override
            public OWLIndividual run() throws ApplicationException {
                return owlService.getIndividual(recordIri);
            }
        });
        /**
         * Сначала получим свойство для изменения
         */
        final OWLProperty targetProperty = wrapperService.invokeWrapped(new ExceptionWrappedCallback<OWLProperty>() {
            @Override
            public OWLProperty run() throws ApplicationException {
                return getTargetProperty(record, propertyName);
            }
        });
        /**
         * Увеличим значение свойства
         * Для этого сначала получим его значение
         * Затем сделаем +1
         * Затем сеттим
         */
        wrapperService.invokeWrapped(new ExceptionWrappedCallback<Integer>() {
            @Override
            public Integer run() throws ApplicationException {
                int currentValue = 0;
                final Object propertyValue = owlService.getPropertyValue(record, targetProperty);
                if (propertyValue != null) {
                    // TODO: 04.06.17 костыли какие-то
                    final OWLLiteralImplInteger integerLiteral = (OWLLiteralImplInteger) propertyValue;
                    currentValue = Integer.parseInt(integerLiteral.getLiteral());
                }
                currentValue++;
                owlService.setPropertyValue(record, targetProperty, currentValue);
                return currentValue;
            }
        });
        /**
         * Сохраним онтологию, чтоб изменения не потерялись
         */
        wrapperService.invokeWrapped(new ExceptionWrappedCallback<Void>() {
            @Override
            public Void run() throws ApplicationException {
                owlService.saveOntology();
                return null;
            }
        });
    }

    /**
     * Получить свойство с указанным именем или создать новое
     * @param record - запись, у которой будем пытаться получить свойство
     * @param propertyName - название свойства
     * @return - старое или новое только что созданное свойство
     * @throws ApplicationException
     */
    private OWLProperty getTargetProperty(OWLIndividual record, String propertyName) throws ApplicationException {
        /**
         * Получим класс, к которому относится экземпляр
         */
        final OWLClass individualClass = owlService.getIndividualClass(record);
        /**
         * Найдем у этого класса требуемое свойство
         */
        final OWLProperty targetProperty = CollectionUtils.findFirst(owlService.getDataProperties(individualClass), new Specification<OWLDataProperty>() {
            @Override
            public boolean isSatisfied(OWLDataProperty property) {
                return StringUtils.equalsIgnoreCase(
                        property.getIRI().getFragment(),
                        propertyName
                );
            }
        });
        /**
         * Если свойство есть - возвращаем его
         */
        if (targetProperty != null) {
            return targetProperty;
        }
        /**
         * Свойства нет - надо его сначала создать
         */
        final Collection<OWLClass> classes = owlService.getIndividualClasses(record);
        return owlService.createDataProperty(classes, propertyName, Integer.class);
    }
}
