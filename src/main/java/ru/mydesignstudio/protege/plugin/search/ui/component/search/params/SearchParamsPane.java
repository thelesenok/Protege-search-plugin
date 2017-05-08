package ru.mydesignstudio.protege.plugin.search.ui.component.search.params;

import com.google.common.eventbus.Subscribe;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.service.search.strategy.StrategyComparator;
import ru.mydesignstudio.protege.plugin.search.ui.event.LookupInstancesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.event.properties.LoadSearchPropertiesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.event.properties.LoadedSearchPropertiesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.event.properties.SaveSearchPropertiesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.event.StrategyChangeEvent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

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
                final Collection<LookupParam> params = getLookupParams(getEnabledStrategies());
                eventBus.publish(new LookupInstancesEvent(params));
            }
        });
        /**
         * добавим также кнопки сохранения и загрузки
         */
        final Button saveButton = new Button("Save");
        final Button loadButton = new Button("Load");
        searchButtonContainer.add(saveButton);
        searchButtonContainer.add(loadButton);
        /**
         * повесим на кнопки обработчики
         */
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final File file = savePropertiesHandler();
                if (file == null) {
                    /**
                     * Пользователь передумал - ничего не делаем
                     */
                    return;
                }
                final Collection<SearchStrategy> enabledStrategies = getEnabledStrategies();
                final Collection<LookupParam> lookupParams = getLookupParams(enabledStrategies);
                eventBus.publish(
                        new SaveSearchPropertiesEvent(
                                file,
                                lookupParams
                        )
                );
            }
        });
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final File file = loadPropertiesHandler();
                if (file == null) {
                    /**
                     * Пользователь передумал - ничего не делаем
                     */
                    return;
                }
                eventBus.publish(new LoadSearchPropertiesEvent(file));
            }
        });
    }

    /**
     * Показать диалог сохранения файла
     * @return - куда сохранять
     */
    private File savePropertiesHandler() {
        return showFileDialog("Save properties");
    }

    /**
     * Показать диалог работы с файлом
     * @param dialogTitle - заголовок диалога
     * @return
     */
    private File showFileDialog(String dialogTitle) {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);
        final int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Показать диалог загрузки файлов
     * @return - выбранный файл
     */
    private File loadPropertiesHandler() {
        return showFileDialog("Load properties");
    }

    /**
     * Параметры поиска, доступные в указанных стратегиях
     * @param strategies - сортированные стратегии
     * @return
     */
    private Collection<LookupParam> getLookupParams(Collection<SearchStrategy> strategies) {
        final Collection<LookupParam> params = new LinkedList<LookupParam>();
        for (SearchStrategy enabledStrategy : strategies) {
            params.add(new LookupParam(
                    enabledStrategy,
                    enabledStrategy.getSearchParamsPane() == null ? null : enabledStrategy.getSearchParamsPane().getSearchParams()
            ));
        }
        return params;
    }

    /**
     * Включенные стратегии в правильном порядке
     * @return
     */
    private Collection<SearchStrategy> getEnabledStrategies() {
        final TreeSet<SearchStrategy> sortedStrategies = new TreeSet<>(new StrategyComparator());
        sortedStrategies.addAll(enabledStrategies);
        return sortedStrategies;
    }

    /**
     * Когда параметры поиска загрузили из файла
     * @param event - событие с данными
     */
    @Subscribe
    public void onSavedParamsLoadListener(LoadedSearchPropertiesEvent event) {
        final Collection<LookupParam> params = event.getLookupParams();
        /**
         * выключаем все предыдущие стратегии
         */
        for (SearchStrategy strategy : enabledStrategies) {
            disableStrategy(strategy);
        }
        /**
         * включим стратегии, загрузим в них параметры
         */
        for (LookupParam lookupParam : params) {
            enableStrategy(lookupParam.getStrategy());
        }
    }

    /**
     * Включить стратегию
     * @param strategy - какую именно
     */
    private void enableStrategy(SearchStrategy strategy) {
        if (enabledStrategies.contains(strategy)) {
            return;
        }
        this.enabledStrategies.add(strategy);
        final Component strategyParamsPane = strategy.getSearchParamsPane();
        if (strategyParamsPane == null) {
            return;
        }
        // добавляем на отдельную закладку
        this.criteriaContainer.addTab(
                strategy.getTitle(),
                strategy.getSearchParamsPane()
        );
    }

    /**
     * Выключить стратегию
     * @param strategy - какую именно
     */
    private void disableStrategy(SearchStrategy strategy) {
        if (strategy.isRequired()) {
            /**
             * обязательные стратегии выключить нельзя
             */
            return;
        }
        this.enabledStrategies.remove(strategy);
        final Component strategyParamsPane = strategy.getSearchParamsPane();
        if (strategyParamsPane == null) {
            return;
        }
        // добавляем на отдельную закладку
        this.criteriaContainer.remove(
                strategy.getSearchParamsPane()
        );
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
        // если это включение стратегии, то добавляем ее в список
        if (event.isSelected()) {
            enableStrategy(selectedStrategy);
        } else {
            disableStrategy(selectedStrategy);
        }
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
