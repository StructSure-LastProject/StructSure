package fr.uge.structsure.utils.sort;

import fr.uge.structsure.entities.Sensor;

import java.util.Comparator;

public class SortByStateStrategy implements SensorSortStrategy {
    @Override
    public Comparator<Sensor> getComparator() {
        return Comparator.comparing(Sensor::getArchived);
    }
}
