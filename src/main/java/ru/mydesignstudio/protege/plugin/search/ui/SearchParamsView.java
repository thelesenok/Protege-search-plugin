package ru.mydesignstudio.protege.plugin.search.ui;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import ru.mydesignstudio.protege.plugin.search.config.OntologyConfig;
import ru.mydesignstudio.protege.plugin.search.ui.component.SearchParamsViewComponent;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import java.awt.BorderLayout;

/**
 * Created by abarmin on 03.01.17.
 */
public class SearchParamsView extends AbstractOWLViewComponent {
    @Override
    protected void initialiseOWLView() throws Exception {
        OntologyConfig.setModelManager(
                getOWLModelManager()
        );

        final SearchParamsViewComponent component = InjectionUtils.getInstance(SearchParamsViewComponent.class);
        //
        setLayout(new BorderLayout());
        add(component, BorderLayout.CENTER);
    }

    @Override
    protected void disposeOWLView() {

    }
}
