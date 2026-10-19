package representation;

import java.util.Arrays;

public class SimpleSolution implements Solution {
    Representation r;
    double[] values;

    public SimpleSolution(Representation r, double value) {
        this.r =r;
        values = new double[1];
        values[0]= value;
    }

    public SimpleSolution(SimpleSolution other) {
        r = other.r.copy();
        values = new double[other.values.length];
        System.arraycopy(other.values,0,values,0,other.values.length);
    }

    @Override
    public Representation getRepresentation() {
        return r;
    }

    @Override
    public double value() {
        return values[0];
    }

    @Override
    public double value(int index) {
        return values[index];
    }

    @Override
    public double[] values() {
        return values;
    }

    @Override
    public Solution copy() {
        return new SimpleSolution(this);
    }

    @Override
    public String toString() {
        return "R:"+ r + "  V:"+ Arrays.toString(values);
    }

    @Override
    public int hashCode() {
        return r.hashCode() + Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof SimpleSolution))
            return false;

        SimpleSolution ss = (SimpleSolution) obj;

        return r.equals(ss.r) && Arrays.equals(values,ss.values);
    }
}