package representation;

/**
 * Represents solutions along with their objective values
 */
public interface Solution {
    Representation getRepresentation();
    double value();
    double value(int index);
    double[] values();

    Solution copy();

    int hashCode();
    boolean equals(Object obj);

}
