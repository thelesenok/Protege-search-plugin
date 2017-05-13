package ru.mydesignstudio.protege.plugin.search.reasoner.sparql;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.protege.owl.rdf.api.OwlTripleStore;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.result.set.SparqlResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abarmin on 07.01.17.
 */
public class GraphQueryHandler implements RDFHandler {
    private OwlTripleStore triples;
    private SparqlResultSet queryResult;

    public GraphQueryHandler(OwlTripleStore triples) {
        this.triples = triples;
    }

    public SparqlResultSet getQueryResult() {
        return queryResult;
    }

    @Override
    public void startRDF() throws RDFHandlerException {
        List<String> bindingNames = new ArrayList<String>();
        bindingNames.add("Subject");
        bindingNames.add("Predicate");
        bindingNames.add("Object");
        queryResult = new SparqlResultSet(bindingNames);
    }

    @Override
    public void handleComment(String arg0) throws RDFHandlerException {

    }

    @Override
    public void handleNamespace(String arg0, String arg1) throws RDFHandlerException {
    }

    @Override
    public void handleStatement(Statement stmt) throws RDFHandlerException {
        try {
            List<Object> row = new ArrayList<Object>();
            row.add(Util.convertValue(triples, stmt.getSubject()));
            row.add(Util.convertValue(triples, stmt.getPredicate()));
            row.add(Util.convertValue(triples, stmt.getObject()));
            queryResult.addRow(row);
        }
        catch (RepositoryException e) {
            throw new RDFHandlerException(e);
        }
    }

    @Override
    public void endRDF() throws RDFHandlerException {

    }
}
