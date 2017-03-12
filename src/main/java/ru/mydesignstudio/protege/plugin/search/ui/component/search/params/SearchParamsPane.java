package ru.mydesignstudio.protege.plugin.search.ui.component.search.params;

import com.google.common.eventbus.Subscribe;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.ui.event.LookupInstancesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.event.StrategyChangeEvent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by abarmin on 03.01.17.
 */
public class SearchParamsPane extends JPanel {
    private JPanel strategiesContainer = new JPanel(new FlowLayout());
    private JTabbedPane criteriaContainer = new JTabbedPane();
    private JPanel searchButtonContainer = new JPanel();

    private Collection<SearchStrategy> enabledStrategies = new HashSet<>();

    @Inject
    private SearchStrategyService strategyService;
    private final EventBus eventBus = EventBus.getInstance();

    public SearchParamsPane() {
        setLayout(new BorderLayout());
        add(strategiesContainer, BorderLayout.NORTH);
        add(criteriaContainer, BorderLayout.CENTER);
        add(searchButtonContainer, BorderLayout.SOUTH);
    }

    @PostConstruct
    public void init() {
        eventBus.register(this);

        fillStrategies();
        fillSearchButton();
        selectRequiredStrategies();

        this.revalidate();
    }

    /**
     * Добавить кнопку поиска
     */
    private void fillSearchButton() {
        final JButton searchButton = new JButton("Search");
        searchButtonContainer.add(searchButton, BorderLayout.CENTER);
        //
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // надо собрать со всех активных способов поиска
                // указанные в них параметры и передать их все
                // в центральный метод поиска
                final ArrayList<LookupParam> params = new ArrayList<>();
                for (SearchStrategy enabledStrategy : enabledStrategies) {
                    params.add(new LookupParam(
                            enabledStrategy,
                            enabledStrategy.getSearchParamsPane().getSearchParams()
                    ));
                }
                eventBus.publish(new LookupInstancesEvent(params));
            }
        });
    }

    /**
     * При включении стратегии добавляем закладку
     * с критериями для конкретного случая
     *
     * @param event
     */
    @Subscribe
    public void onStrategyChangeEventListener(StrategyChangeEvent event) {
        final SearchStrategy selectedStrategy = event.getStrategy();
        final Component strategyParamsPane = selectedStrategy.getSearchParamsPane();
        // если это включение стратегии, то добавляем ее в список
        if (event.isSelected()) {
            this.enabledStrategies.add(selectedStrategy);
        } else {
            this.enabledStrategies.remove(selectedStrategy);
        }
        //
        if (strategyParamsPane == null) {
            return;
        }
        // добавляем на отдельную закладку
        this.criteriaContainer.addTab(
                selectedStrategy.getTitle(),
                selectedStrategy.getSearchParamsPane()
        );
    }

    /**
     * Отметить обязательные стратегии галочками -
     * опубликовать события
     */
    private void selectRequiredStrategies() {
        final Collection<SearchStrategy> strategies = strategyService.getStrategies();
        for (SearchStrategy strategy : strategies) {
            if (strategy.isRequired()) {
                eventBus.publish(new StrategyChangeEvent(strategy, true));
            }
        }
    }

    /**
     * Заполнить список доступных стратегий
     */
    private void fillStrategies() {
        final Collection<SearchStrategy> strategies = strategyService.getStrategies();
        for (final SearchStrategy strategy : strategies) {
            final JCheckBox strategySelectFlag = new JCheckBox(strategy.getTitle(), strategy.isRequired());
            strategySelectFlag.setEnabled(!strategy.isRequired());
            strategiesContainer.add(strategySelectFlag);
            //
            strategySelectFlag.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    eventBus.publish(new StrategyChangeEvent(strategy, e.getStateChange() == ItemEvent.SELECTED));
                }
            });
        }
    }
}
