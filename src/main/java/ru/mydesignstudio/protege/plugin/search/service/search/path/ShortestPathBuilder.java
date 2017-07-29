package ru.mydesignstudio.protege.plugin.search.service.search.path;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.PropertyBasedPathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.Transformer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class ShortestPathBuilder implements PropertyBasedPathBuilder {
	private final OWLService owlService;
	private final ExceptionWrapperService wrapperService;
	
	@Inject
	public ShortestPathBuilder(OWLService owlService, ExceptionWrapperService wrapperService) {
		this.owlService = owlService;
		this.wrapperService = wrapperService;
	}

	@Override
	public Collection<SelectField> buildPath(OWLClass sourceClass, OWLClass destinationClass) throws ApplicationException {
		/**
		 * Используем поиск в ширину для нахождения путей между классами. 
		 * Строим граф, где вершинами являются классы, а дугам (направленными стрелками) будут объектные свойства.
		 *
		 * Сначала проверим, достижи ли целевой класс вообще, построим путь только по вершинам
		 */
		final List<OWLClass> targetPath = buildVertexPath(sourceClass, destinationClass);
		/**
		 * Построим из этого путь по свойствам
		 */
		return buildPropertyPath(targetPath);
	}

	private Collection<SelectField> buildPropertyPath(List<OWLClass> targetPath) throws ApplicationException {
		if (targetPath.isEmpty() || targetPath.size() == 1) {
			/**
			 * Целевая и исходная вершины совпадают - пути по свойствам не надо
			 */
			return Collections.emptyList();
		}
		final Collection<SelectField> propertyPath = new ArrayList<>();
		for (int index = 0; index < targetPath.size() - 1; index++) {
			propertyPath.add(buildPropertyPath(
					targetPath.get(index),
					targetPath.get(index + 1)
			));
		}
		return propertyPath;
	}

	private SelectField buildPropertyPath(OWLClass sourceClass, OWLClass targetClass) throws ApplicationException {
		final Collection<OWLObjectProperty> properties = owlService.getObjectProperties(sourceClass);
		for (OWLObjectProperty property : properties) {
			for (OWLPropertyRange range : owlService.getPropertyRanges(property)) {
				for (OWLClass relatedClass : range.getClassesInSignature()) {
					if (OWLUtils.equals(relatedClass, targetClass)) {
						return new SelectField(
								sourceClass,
								property
						);
					}
				}
			}
		}
		throw new ApplicationException(String.format(
				"Can't find relational property between %s and %s",
				sourceClass,
				targetClass
		));
	}

	private List<OWLClass> buildVertexPath(OWLClass sourceClass, OWLClass destinationClass) throws ApplicationException {
		/**
		 * Построим все доступные пути между source и destination
		 */
		final Collection<List<OWLClass>> allPaths = new ArrayList<>();
		buildVertexPath(sourceClass, destinationClass, new ArrayList<>(), allPaths);
		/**
		 * Проверим, что есть путь из source в destination
		 */
		final Collection<List<OWLClass>> suitablePaths = CollectionUtils.filter(allPaths, new Specification<List<OWLClass>>() {
			@Override
			public boolean isSatisfied(List<OWLClass> path) {
				return isDestinationReached(path, destinationClass);
			}
		});
		if (suitablePaths.isEmpty()) {
			throw new ApplicationException(String.format(
					"There is no path from %s to %s",
					sourceClass,
					destinationClass
			));
		}
		return CollectionUtils.min(suitablePaths, new Comparator<List<OWLClass>>() {
			@Override
			public int compare(List<OWLClass> first, List<OWLClass> second) {
				return Integer.compare(first.size(), second.size());
			}
		});
	}

	private void buildVertexPath(OWLClass currentClass, OWLClass destinationClass,
								 Collection<OWLClass> currentPath, Collection<List<OWLClass>> allPaths) throws ApplicationException {

		final List<OWLClass> thisPath = new ArrayList<>(currentPath);
		if (currentPath.contains(currentClass)) {
			return;
		}
		thisPath.add(currentClass);
		allPaths.add(thisPath);
		//
		if (!OWLUtils.equals(currentClass, destinationClass)) {
			for (OWLClass relatedClass : getRelatedClasses(currentClass)) {
				buildVertexPath(relatedClass, destinationClass, thisPath, allPaths);
			}
		}
	}

	private Collection<OWLClass> getRelatedClasses(OWLClass currentClass) throws ApplicationException {
		final Collection<OWLObjectProperty> properties = owlService.getObjectProperties(currentClass);
		final Collection<OWLPropertyRange> ranges = CollectionUtils.flatMap(properties, new Transformer<OWLObjectProperty, Collection<OWLPropertyRange>>() {
			@Override
			public Collection<OWLPropertyRange> transform(OWLObjectProperty item) {
				return wrapperService.invokeWrapped(new ExceptionWrappedCallback<Collection<OWLPropertyRange>>() {
					@Override
					public Collection<OWLPropertyRange> run() throws ApplicationException {
						return owlService.getPropertyRanges(item);
					}
				});
			}
		});
		return CollectionUtils.flatMap(ranges, new Transformer<OWLPropertyRange, Collection<OWLClass>>() {
			@Override
			public Collection<OWLClass> transform(OWLPropertyRange item) {
				return item.getClassesInSignature();
			}
		});
	}

	private boolean isDestinationReached(Collection<OWLClass> currentPath, OWLClass destinationClass) {
		if (currentPath.isEmpty()) {
			return false;
		}
		OWLClass lastItem = null;
		for (OWLClass selectField : currentPath) {
			lastItem = selectField;
		}
		return OWLUtils.equals(
				lastItem,
				destinationClass
		);
	}
}
