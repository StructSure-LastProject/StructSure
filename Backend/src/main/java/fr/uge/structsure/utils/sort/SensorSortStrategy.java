package fr.uge.structsure.utils.sort;

import fr.uge.structsure.entities.Sensor;

import java.util.Comparator;

public interface SensorSortStrategy {
    Comparator<Sensor> getComparator();
}
