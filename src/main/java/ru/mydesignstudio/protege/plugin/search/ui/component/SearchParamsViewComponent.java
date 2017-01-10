package ru.mydesignstudio.protege.plugin.search.ui.component;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.ui.event.LookupInstancesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.event.StrategyChangeEvent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by abarmin on 03.01.17.
 */
public class SearchParamsViewComponent extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchParamsViewComponent.class);

    private JPanel strategiesContainer = new JPanel(new FlowLayout());
    private JPanel criteriaContainer = new JPanel();
    private JPanel searchButtonContainer = new JPanel();
    private SearchStrategyComponent searchStrategyComponent;

    @Inject
    private SearchStrategyService strategyService;

    private EventBus eventBus = EventBus.getInstance();

    public SearchParamsViewComponent() {
        setLayout(new BorderLayout());
        add(strategiesContainer, BorderLayout.NORTH);
        add(searchButtonContainer, BorderLayout.SOUTH);
    }

    @PostConstruct
    public void init() {
        eventBus.register(this);

        fillStrategies();
        selectFirstStrategyIfAvailable();
        fillSearchButton();
    }

    private void fillSearchButton() {
        final JButton searchButton = new JButton("Search");
        searchButtonContainer.add(searchButton, BorderLayout.CENTER);
        //
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eventBus.publish(new LookupInstancesEvent(
                        searchStrategyComponent.getSelectQuery()
                ));
            }
        });
    }

    @Subscribe
    public void onStrategyChangeEventListener(StrategyChangeEvent event) {
        try {
            final Component paramsPane = event.getStrategy().getSearchParamsPane();
            if (paramsPane != null) {
                criteriaContainer = new JPanel(new BorderLayout());
                add(criteriaContainer, BorderLayout.CENTER);
                criteriaContainer.add(paramsPane, BorderLayout.CENTER);
                //
                searchStrategyComponent = (SearchStrategyComponent) paramsPane;
                //
                this.revalidate();
            }
        } catch (Exception e) {
            LOGGER.warn("Can't change search params component", e);
        }
    }

    private void selectFirstStrategyIfAvailable() {
        final Collection<SearchStrategy> strategies = strategyService.getStrategies();
        final Iterator<SearchStrategy> iterator = strategies.iterator();
        if (iterator.hasNext()) {
            final SearchStrategy firstStrategy = iterator.next();
            eventBus.publish(new StrategyChangeEvent(firstStrategy));
        }
    }

    private void fillStrategies() {
        final Collection<SearchStrategy> strategies = strategyService.getStrategies();
        final ButtonGroup buttonGroup = new ButtonGroup();
        boolean isFirst = true;
        for (final SearchStrategy strategy : strategies) {
            final JRadioButton strategySelectButton = new JRadioButton(strategy.getTitle(), isFirst);
            // обработчик события выбора
            strategySelectButton.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        eventBus.publish(new StrategyChangeEvent(strategy));
                    }
                }
            });
            // помещаем в контейнер
            buttonGroup.add(strategySelectButton);
            strategiesContainer.add(strategySelectButton);
            //
            if (isFirst) {
                isFirst = false;
            }
        }
    }
}
