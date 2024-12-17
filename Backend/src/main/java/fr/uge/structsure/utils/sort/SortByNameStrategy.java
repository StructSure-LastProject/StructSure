package fr.uge.structsure.utils.sort;

import fr.uge.structsure.entities.Sensor;

import java.util.Comparator;

public class SortByNameStrategy implements SensorSortStrategy {
    @Override
    public Comparator<Sensor> getComparator() {
        return Comparator.comparing(Sensor::getName);
    }
}
