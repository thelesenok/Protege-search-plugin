package ru.mydesignstudio.protege.plugin.search.api.search;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;

import java.awt.*;

/**
 * Created by abarmin on 03.01.17.
 *
 * Стратегия поиска
 */
public interface SearchStrategy {
    /**
     * Название стратегии поиска
     *
     * @return
     */
    String getTitle();

    /**
     * Get search strategy params.
     * @return strategy params
     */
    SearchProcessorParams getSearchStrategyParams();

    /**
     * Панель конфигурации поиска
     *
     * @return
     */
    <T extends Component & SearchStrategyComponent> T getSearchParamsPane();

    /**
     * Can be disabled
     * @throws ApplicationException
     * @return
     */
    boolean canBeDisabled() throws ApplicationException;

    /**
     * Is strategy enabled by default
     * @throws ApplicationException
     * @return
     */
    boolean enabledByDefault() throws ApplicationException;

    /**
     * Порядок сортировки
     *
     * @return
     */
    int getOrder();

    /**
     * Компонент, который непосредственно выполняет поиск
     * по указанной стратегии
     *
     * @return
     */
    SearchProcessor getSearchProcessor();
}
