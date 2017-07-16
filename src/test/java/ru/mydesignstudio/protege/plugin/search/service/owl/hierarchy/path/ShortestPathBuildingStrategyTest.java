package ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.node.NodeStorage;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.node.NodeStorageFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

@RunWith(MockitoJUnitRunner.class)
public class ShortestPathBuildingStrategyTest {
	@Mock
	private NodeStorageFactory storageFactory;
	private PathBuildingStrategy strategy;
	
	@Before
	public void setUp() throws Exception {
		/** Add classes hierarchy. Test example is the following:
		 * A -> B, A -> C, B -> A, B -> C, C -> D */
		final NodeStorage storage = new NodeStorage();
		storage.addChildren(createClass("A"), createClass("B"));
		storage.addChildren(createClass("A"), createClass("C"));
		storage.addChildren(createClass("B"), createClass("A"));
		storage.addChildren(createClass("B"), createClass("C"));
		storage.addChildren(createClass("C"), createClass("D"));
		doReturn(storage).when(storageFactory).build();
		/** Prepare testable class */
		strategy = new ShortestPathBuildingStrategy(storageFactory);
	}
	
	private OWLClass createClass(String className) {
		return new OWLClassImpl(IRI.create("dummyNamespace", className));
	}

	@Test
	public void testPathFromSameClasses() throws Exception {
		final Collection<OWLClass> path = strategy.build(createClass("A"), createClass("A"));
		assertEquals("Path lenght check failed", 1, path.size());
		assertEquals("Path check failed", Arrays.asList(createClass("A")), path);
	}

	@Test
	public void testNeighbourClasses() throws Exception {
		final Collection<OWLClass> path = strategy.build(createClass("A"), createClass("B"));
		assertEquals("Path lenght check failed", 2, path.size());
		assertEquals("Path check failed", Arrays.asList(createClass("A"), createClass("B")), path);
	}
	
	@Test
	public void testLongPath() throws Exception {
		final Collection<OWLClass> path = strategy.build(createClass("A"), createClass("D"));
		assertEquals("Path lenght check fialed", 3, path.size());
		assertEquals("Path check failed", Arrays.asList(
				createClass("A"),
				createClass("C"),
				createClass("D")
				), path);
	}
	
	@Test(expected = ApplicationException.class)
	public void testUnavailablePath() throws Exception {
		final Collection<OWLClass> path = strategy.build(createClass("C"), createClass("A"));
	}
}
