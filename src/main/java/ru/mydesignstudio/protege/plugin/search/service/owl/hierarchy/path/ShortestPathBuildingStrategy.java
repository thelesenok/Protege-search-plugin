package ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.OWLClass;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.node.Node;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.node.NodeStorage;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.node.NodeStorageFactory;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.Transformer;

/**
 * Strategy for searching paths between classes
 * @author abarmin
 */
@Component
public class ShortestPathBuildingStrategy implements PathBuildingStrategy {
	private final NodeStorageFactory storageFactory;
	
	@Inject
	public ShortestPathBuildingStrategy(NodeStorageFactory storageFactory) {
		this.storageFactory = storageFactory;
	}

	@Override
	public Collection<OWLClass> build(OWLClass sourceClass, OWLClass destinationClass) throws ApplicationException {
		/** Building storage at first */
		final NodeStorage storage = storageFactory.build();
		/** Check is storage contains source and destination classes */
		if (!storage.contains(sourceClass)) {
			throw new ApplicationException(String.format(
					"Storage doesn't contains source class %s", 
					sourceClass
					));
		}
		if (!storage.contains(destinationClass)) {
			throw new ApplicationException(String.format(
					"Storage doesn't contains destination class %s", 
					destinationClass
					));
		}
		/** Searching for path from source to destination using stupidest algorithm */
		final Node sourceNode = storage.getNode(sourceClass);
		final Node destinationNode = storage.getNode(destinationClass);
		final Collection<Collection<Node>> allPaths = new ArrayList<>();
		buildAllPaths(sourceNode, destinationNode, new ArrayList<>(), allPaths);
		/** Check is destination reached */
		final Collection<Collection<Node>> suitablePaths = CollectionUtils.filter(allPaths, new Specification<Collection<Node>>() {
			@Override
			public boolean isSatisfied(Collection<Node> path) {
				return isDestinationReached(destinationNode, path);
			}
		});
		if (CollectionUtils.isEmpty(suitablePaths)) {
			throw new ApplicationException(String.format(
					"There is no path from %s to %s",
					sourceClass,
					destinationClass
					));
		}
		/** Select the shortest path */
		final Collection<Node> shortestNodePath = CollectionUtils.min(suitablePaths, new Comparator<Collection<Node>>() {
			@Override
			public int compare(Collection<Node> first, Collection<Node> second) {
				return Integer.compare(first.size(), second.size());
			}
			
		});
		return CollectionUtils.map(shortestNodePath, new Transformer<Node, OWLClass>() {
			@Override
			public OWLClass transform(Node node) {
				return node.getNodeClass();
			}
		});
	}

	/**
	 * Build all paths in the nodes graph
	 * @param currentNode current class
	 * @param destinationNode destination class
	 * @param currentPath current partial path from source to destination classes
	 * @param allPaths all found paths
	 */
	private void buildAllPaths(Node currentNode, Node destinationNode,
			Collection<Node> currentPath, Collection<Collection<Node>> allPaths) {
		
		final Collection<Node> thisPath = new ArrayList<>(currentPath);
		/** Check sourceClass already visited */
		if (thisPath.contains(currentNode)) {
			return;
		}
		thisPath.add(currentNode);
		allPaths.add(thisPath);
		/** Check is destination reached */
		if (OWLUtils.equals(currentNode.getNodeClass(), destinationNode.getNodeClass())) {
			return;
		}
		/** Build other paths */
		for (Node node : currentNode.getChildren()) {
			buildAllPaths(node, destinationNode, thisPath, allPaths);
		}
	}
	
	/**
	 * Check is destination in path reached
	 * @param destinationNode node to search for
	 * @param path path to check
	 * @return is destination in path reached
	 */
	private boolean isDestinationReached(Node destinationNode, Collection<Node> path) {
		if (path.isEmpty()) {
			return false;
		}
		Node currentNode = null;
		for (Node node : path) {
			currentNode = node;
		}
		return OWLUtils.equals(
				destinationNode.getNodeClass(), 
				currentNode.getNodeClass()
				);
	}
}
