package representation;

public interface Representation {
    Representation copy();

    int hashCode();
    boolean equals(Object obj);
}