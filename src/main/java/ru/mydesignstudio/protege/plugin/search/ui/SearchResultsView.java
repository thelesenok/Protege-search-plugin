package ru.mydesignstudio.protege.plugin.search.ui;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import ru.mydesignstudio.protege.plugin.search.ui.component.SearchResultsViewComponent;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import java.awt.BorderLayout;

/**
 * Created by abarmin on 03.01.17.
 */
public class SearchResultsView extends AbstractOWLViewComponent {
    @Override
    protected void initialiseOWLView() throws Exception {
        final SearchResultsViewComponent viewComponent = InjectionUtils.getInstance(SearchResultsViewComponent.class);
        //
        setLayout(new BorderLayout());
        add(viewComponent, BorderLayout.CENTER);
    }

    @Override
    protected void disposeOWLView() {

    }
}
