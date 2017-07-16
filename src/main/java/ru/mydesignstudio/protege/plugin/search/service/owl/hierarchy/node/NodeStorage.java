package ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.node;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLClass;

import ru.mydesignstudio.protege.plugin.search.api.common.Validation;

/**
 * Storage for classes hierarchy
 * @author abarmin
 */
public class NodeStorage {
	private final Map<String, Node> storage = new HashMap<>();
	
	/**
	 * Add child class to parent node
	 * @param parent parent class
	 * @param child child class
	 */
	public void addChildren(OWLClass parent, OWLClass child) {
		Validation.assertNotNull("Parent class is not set", parent);
		Validation.assertNotNull("Child class is not set", child);
		/** Try to get parent class node */
		Node parentNode = storage.get(getClassName(parent));
		if (parentNode == null) {
			parentNode = new Node(parent);
			storage.put(getClassName(parent), parentNode);
		}
		/** Try to get child class node */
		Node childNode = storage.get(getClassName(child));
		if (childNode == null) {
			childNode = new Node(child);
			storage.put(getClassName(child), childNode);
		}
		/** Add child node to parent */
		parentNode.addChild(childNode);
	}
	
	/**
	 * Get node for given class
	 * @param owlClass class node we are looking for
	 * @return class node
	 */
	public Node getNode(OWLClass owlClass) {
		return storage.get(getClassName(owlClass));
	}
	
	/**
	 * Get string presentation of class name
	 * @param owlClass ontology class
	 * @return class name
	 */
	private String getClassName(OWLClass owlClass) {
		return owlClass.getIRI().getFragment();
	}
	
	/**
	 * Check if storage contains node with desired class
	 * @param owlClass class to search for
	 * @return result of searching
	 */
	public boolean contains(OWLClass owlClass) {
		return storage.containsKey(getClassName(owlClass));
	}
}
