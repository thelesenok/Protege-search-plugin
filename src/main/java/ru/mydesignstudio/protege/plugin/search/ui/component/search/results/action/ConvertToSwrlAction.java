package ru.mydesignstudio.protege.plugin.search.ui.component.search.results.action;

import org.semanticweb.owlapi.model.IRI;
import org.swrlapi.core.SWRLAPIRule;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.SwrlService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.utils.Action;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертировать в SWRL
 */
public class ConvertToSwrlAction implements Action<Integer> {
    private final ResultSet resultSet;
    private final Collection<LookupParam> lookupParams;

    private final SwrlService swrlService;
    private final ExceptionWrapperService wrapperService;
    private final OWLService owlService;

    public ConvertToSwrlAction(ResultSet resultSet, Collection<LookupParam> lookupParams) {
        this.resultSet = resultSet;
        this.lookupParams = lookupParams;
        //
        swrlService = InjectionUtils.getInstance(SwrlService.class);
        wrapperService = InjectionUtils.getInstance(ExceptionWrapperService.class);
        owlService = InjectionUtils.getInstance(OWLService.class);
    }

    /**
     * Сгенерировать псевдослучайное имя для правила
     * @return
     */
    private String createRuleName() {
        return "Created automatically " +
                new SimpleDateFormat("ddMMyyyy HHmms").format(new Date());
    }

    @Override
    public void run(Integer rowIndex) {
        /**
         * Определим запись по номеру строки
         */
        final IRI recordIri = (IRI) resultSet.getResult(rowIndex, resultSet.getColumnIndex(FieldConstants.OBJECT_IRI));
        /**
         * Конвертируем в SWRL
         */
        final String swrlString = wrapperService.invokeWrapped(new ExceptionWrappedCallback<String>() {
            @Override
            public String run() throws ApplicationException {
                return swrlService.convertToSwrl(recordIri, lookupParams);
            }
        });
        /**
         * Создаем правило в онтологии
         */
        final SWRLAPIRule swrlRule = wrapperService.invokeWrapped(new ExceptionWrappedCallback<SWRLAPIRule>() {
            @Override
            public SWRLAPIRule run() throws ApplicationException {
                return swrlService.createSwrlRule(createRuleName(), swrlString);
            }
        });
        /**
         * Сохраняем онтологию
         */
        wrapperService.invokeWrapped(new ExceptionWrappedCallback<Void>() {
            @Override
            public Void run() throws ApplicationException {
                owlService.saveOntology();
                return null;
            }
        });
    }
}
