package fr.uge.structsure.utils;

import java.util.function.Supplier;

/**
 * Internal class to build diff summary between a previous value and
 * updates values that can then be logged.
 */
public class DiffMaker {
    private final StringBuilder builder = new StringBuilder();

    /**
     * Compares the values and adds a line in the diff builder if they
     * are different.
     * @param name the name of the attribute that got changed
     * @param before the value before changes
     * @param after the new value
     * @return the current object
     */
    public DiffMaker add(String name, String before, String after) {
        if (!before.equals(after)) {
            builder.append("\n").append(name).append(": '")
                .append(before).append("' -> '").append(after).append("'");
        }
        return this;
    }

    /**
     * Adds a line in the diff builder
     * @param changed whether the value has changed or not
     * @param line adds manually a new line for values that are not
     *             allowed to be logged (such as password)
     * @return the current object
     */
    public DiffMaker add(boolean changed, Supplier<String> line) {
        if (changed) builder.append("\n").append(line);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
