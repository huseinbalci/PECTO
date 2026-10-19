package representation;

/**
 * Represents Permutational solutions
 * @param <T>
 */
public interface Permutation<T>  extends Representation {
    T get(int index);
    int size();

    void swap(int i1, int i2);

    Permutation copy();
}
