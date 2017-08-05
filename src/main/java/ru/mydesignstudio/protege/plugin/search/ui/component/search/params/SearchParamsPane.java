package ru.mydesignstudio.protege.plugin.search.ui.component.search.params;

import com.google.common.eventbus.Subscribe;
import ru.mydesignstudio.protege.plugin.search.api.annotation.VisualComponent;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategySerializationService;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.service.search.strategy.StrategyComparator;
import ru.mydesignstudio.protege.plugin.search.ui.event.LookupInstancesEvent;
import ru.mydesignstudio.protege.plugin.search.ui.event.StrategyChangeEvent;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
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
@VisualComponent
@SuppressWarnings("serial")
public class SearchParamsPane extends JPanel {
    private final JPanel strategiesContainer = new JPanel(new FlowLayout());
    private final JTabbedPane criteriaContainer = new JTabbedPane();
    private final JPanel searchButtonContainer = new JPanel();

    private final Collection<SearchStrategy> enabledStrategies = new HashSet<>();
    private final Collection<JCheckBox> strategySelectors = new HashSet<>();

    private final SearchStrategyService strategyService;
    private final SearchStrategySerializationService serializationService;
    private final ExceptionWrapperService wrapperService;

    private final EventBus eventBus = EventBus.getInstance();

    @Inject
    public SearchParamsPane(SearchStrategyService strategyService,
			SearchStrategySerializationService serializationService, ExceptionWrapperService wrapperService) {
		this.strategyService = strategyService;
		this.serializationService = serializationService;
		this.wrapperService = wrapperService;
		//
		setLayout(new BorderLayout());
		add(strategiesContainer, BorderLayout.NORTH);
		add(criteriaContainer, BorderLayout.CENTER);
		add(searchButtonContainer, BorderLayout.SOUTH);
	}

    @PostConstruct
    public void init() {
        eventBus.register(this);

        wrapperService.invokeWrapped(new ExceptionWrappedCallback<Void>() {
            @Override
            public Void run() throws ApplicationException {
                fillStrategies();
                selectRequiredStrategies();
                return null;
            }
        });

        fillSearchButton();
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
                /**
                 * сохраняем все через сервим
                 */
                wrapperService.invokeWrapped(new ExceptionWrappedCallback<Void>() {
                    @Override
                    public Void run() throws ApplicationException {
                        serializationService.save(file, lookupParams);
                        return null;
                    }
                });
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
                /**
                 * загружаем через сервис
                 */
                final Collection<LookupParam> params = wrapperService.invokeWrapped(new ExceptionWrappedCallback<Collection<LookupParam>>() {
                    @Override
                    public Collection<LookupParam> run() throws ApplicationException {
                        return serializationService.load(file);
                    }
                });
                applyLookupParams(params);
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
     * Параметры поиска, доступные в¶ указанных стратегиях
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
     * Применяем загруженные параметры
     * @param params - параметры
     */
    public void applyLookupParams(Collection<LookupParam> params) {
        /**
         * выключаем все предыдущие стратегии
         */
        for (SearchStrategy strategy : enabledStrategies) {
            disableStrategyCheckbox(strategy);
        }
        /**
         * включим стратегии, загрузим в них параметры
         */
        for (LookupParam lookupParam : params) {
            enableStrategyCheckbox(lookupParam.getStrategy());
            applyStrategyParams(lookupParam.getStrategy(), lookupParam.getStrategyParams());
        }
    }

    /**
     * Применить к указанной стратегии параметры
     * @param strategy - вот к этой стратегии
     * @param params - эти параметры
     */
    private void applyStrategyParams(SearchStrategy strategy, SearchProcessorParams params) {
        wrapperService.invokeWrapped(new ExceptionWrappedCallback<Void>() {
            @Override
            public Void run() throws ApplicationException {
                final SearchStrategy enabledStrategy = strategyService.getStrategy(strategy.getClass());
                final SearchStrategyComponent paramsPane = enabledStrategy.getSearchParamsPane();
                if (paramsPane != null) {
                    paramsPane.setSearchParams(params);
                }
                return null;
            }
        });
    }

    /**
     * Есть ли уже такая включенная стратегия
     * @param toCheck - проверяем, включена ли вот эта
     * @return
     */
    private boolean isStrategyEnabled(SearchStrategy toCheck) {
        if (enabledStrategies.contains(toCheck)) {
            return true;
        }
        return CollectionUtils.some(enabledStrategies, new Specification<SearchStrategy>() {
            @Override
            public boolean isSatisfied(SearchStrategy strategy) {
                return strategy.getClass().equals(toCheck.getClass());
            }
        });
    }

    /**
     * Включаем галку стратегии руками
     * @param strategy
     */
    private void enableStrategyCheckbox(SearchStrategy strategy) {
        for (JCheckBox selector : strategySelectors) {
            if (StringUtils.equalsIgnoreCase(selector.getText(), strategy.getTitle())) {
                selector.setSelected(true);
            }
        }
    }

    /**
     * Выключаем галку стратегии
     * @param strategy
     */
    private void disableStrategyCheckbox(SearchStrategy strategy) {
        for (JCheckBox selector : strategySelectors) {
            if (StringUtils.equalsIgnoreCase(selector.getText(), strategy.getTitle())) {
                selector.setSelected(false);
            }
        }
    }

    /**
     * Включить стратегию
     * @param strategy - какую именно
     */
    private void enableStrategy(SearchStrategy strategy) throws ApplicationException {
        /**
         * не включаем стратегию, если она уже включена
         */
        if (isStrategyEnabled(strategy)) {
            return;
        }
        /**
         * добавляем в список включенных
         */
        enabledStrategies.add(strategy);
        final Component strategyParamsPane = strategy.getSearchParamsPane();
        if (strategyParamsPane == null) {
            return;
        }
        /**
         * добавляем на отдельную закладку
         */
        criteriaContainer.addTab(
                strategy.getTitle(),
                strategy.getSearchParamsPane()
        );
    }

    /**
     * Выключить стратегию
     * @param strategy - какую именно
     */
    private void disableStrategy(SearchStrategy strategy) throws ApplicationException {
        if (!strategy.canBeDisabled()) {
            /**
             * обязательные стратегии выключить нельзя
             */
            return;
        }
        /**
         * выключим галку
         */
        for (JCheckBox selector : strategySelectors) {
            if (StringUtils.equalsIgnoreCase(selector.getText(), strategy.getTitle())) {
                selector.setSelected(false);
            }
        }
        /**
         * убираем из списка включенных
         */
        this.enabledStrategies.remove(strategy);
        final Component strategyParamsPane = strategy.getSearchParamsPane();
        if (strategyParamsPane == null) {
            return;
        }
        /**
         * убираем отдельную закладку
         */
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
        wrapperService.invokeWrapped(new ExceptionWrappedCallback<Void>() {
            @Override
            public Void run() throws ApplicationException {
                if (event.isSelected()) {
                    enableStrategy(selectedStrategy);
                } else {
                    disableStrategy(selectedStrategy);
                }
                return null;
            }
        });
    }

    /**
     * Отметить обязательные стратегии галочками -
     * опубликовать события
     */
    private void selectRequiredStrategies() throws ApplicationException {
        final Collection<SearchStrategy> strategies = strategyService.getStrategies();
        for (SearchStrategy strategy : strategies) {
            if (strategy.enabledByDefault()) {
                eventBus.publish(new StrategyChangeEvent(strategy, true));
            }
        }
    }

    /**
     * Заполнить список доступных стратегий
     */
    private void fillStrategies() throws ApplicationException {
        final Collection<SearchStrategy> strategies = strategyService.getStrategies();
        for (final SearchStrategy strategy : strategies) {
            final JCheckBox strategySelectFlag = new JCheckBox(strategy.getTitle(), strategy.enabledByDefault());
            strategySelectFlag.setEnabled(strategy.canBeDisabled());
            strategiesContainer.add(strategySelectFlag);
            strategySelectors.add(strategySelectFlag);
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
