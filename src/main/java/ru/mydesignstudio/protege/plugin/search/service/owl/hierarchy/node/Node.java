package ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.node;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLClass;

/**
 * Node in hierarchy
 * @author abarmin
 */
public class Node {
	private final OWLClass nodeClass;
	private final Collection<Node> children = new HashSet<>();
	
	public Node(OWLClass nodeClass) {
		this.nodeClass = nodeClass;
	}
	
	/**
	 * Add child node to current
	 * @param childNode child node
	 */
	public void addChild(Node childNode) {
		children.add(childNode);
	}

	public OWLClass getNodeClass() {
		return nodeClass;
	}

	public Collection<Node> getChildren() {
		return Collections.unmodifiableCollection(children);
	}
}
