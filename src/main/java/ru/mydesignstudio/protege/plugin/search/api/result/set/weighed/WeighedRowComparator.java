package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;

import java.util.Comparator;

/**
 * Created by abarmin on 06.05.17.
 *
 * Компаратор для сортировки строк во взвешенных результатах поиска
 */
public class WeighedRowComparator implements Comparator<WeighedRow> {
    @Override
    public int compare(WeighedRow o1, WeighedRow o2) {
        throw new ApplicationRuntimeException("Not implemented yet");
//        if (o1.getProximityFactor() - o2.getProximityFactor() == 0) {
//            return 0;
//        } else if (o1.getProximityFactor() - o2.getProximityFactor() > 0) {
//            return 1;
//        } else {
//            return -1;
//        }
    }
}
