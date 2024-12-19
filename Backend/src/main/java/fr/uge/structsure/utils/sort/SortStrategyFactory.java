package fr.uge.structsure.utils.sort;

import java.util.Map;

public class SortStrategyFactory {

    private static final Map<String, SensorSortStrategy> STRATEGIES = Map.of(
            "nom", new SortByNameStrategy(),
            "dateInstallation", new SortByInstallationDateStrategy()
    );

    public static SensorSortStrategy getStrategy(String criteria) {
        SensorSortStrategy strategy = STRATEGIES.get(criteria.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Critère de tri invalide : " + criteria);
        }
        return strategy;
    }
}
