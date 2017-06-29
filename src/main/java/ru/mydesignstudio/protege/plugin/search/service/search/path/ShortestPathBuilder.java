package ru.mydesignstudio.protege.plugin.search.service.search.path;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.OWLClass;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.PathBuilder;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;

@Component
public class ShortestPathBuilder implements PathBuilder {
	private final OWLService owlService;
	
	@Inject
	public ShortestPathBuilder(OWLService owlService) {
		this.owlService = owlService;
	}

	@Override
	public Collection<SelectField> buildPath(OWLClass sourceClass, OWLClass destinationClass) throws ApplicationException {
		/**
		 * Сначала проверим, что это разные классы
		 */
		if (OWLUtils.equals(sourceClass, destinationClass)) {
			/**
			 * Это один и тот же класс, здесь все доступно сразу
			 */
			return Collections.emptyList();
		}
		/**
		 * Используем поиск в ширину для нахождения путей между классами. 
		 * Строим граф, где вершинами являются классы, а дугам (направленными стрелками) будут объектные свойства.
		 */
		return Collections.emptyList();
	}

	/**
	 * Один шаг поиска пути
	 * @param currentClass - текущий класс
	 * @param destinationClass - к какому классу идем
	 * @param usedVertexes - коллекция уже использованных вершин
	 * @param currentPath - текущий набранный путь
	 * @throws ApplicationException
	 */
	private void buildPath(OWLClass currentClass, OWLClass destinationClass, 
			Collection<OWLClass> usedVertexes, Collection<OWLClass> currentPath) throws ApplicationException {
		
	}
}
