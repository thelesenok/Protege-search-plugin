package ru.mydesignstudio.protege.plugin.search.ui.component.search.results.action;

import org.semanticweb.owlapi.model.IRI;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.SwrlService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.utils.Action;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import java.util.Collection;

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

    public ConvertToSwrlAction(ResultSet resultSet, Collection<LookupParam> lookupParams) {
        this.resultSet = resultSet;
        this.lookupParams = lookupParams;
        //
        swrlService = InjectionUtils.getInstance(SwrlService.class);
        wrapperService = InjectionUtils.getInstance(ExceptionWrapperService.class);
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
         * Пока будем в консоль выводить
         */
        System.out.println(swrlString);
    }
}
