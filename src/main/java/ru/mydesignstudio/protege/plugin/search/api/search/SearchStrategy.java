package ru.mydesignstudio.protege.plugin.search.api.search;

import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;

import java.awt.Component;

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
     * Панель конфигурации поиска
     *
     * @return
     */
    <T extends Component & SearchStrategyComponent> T getSearchParamsPane();

    /**
     * Обязательная, не может быть выключена
     *
     * @return
     */
    boolean isRequired();

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
