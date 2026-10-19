package representation;



import java.util.Arrays;

public class RealVector implements Representation {

    double[] nodes;

    public RealVector(double[] nodes) {
        this.nodes = nodes;
    }


    public int len()
    {
        return nodes.length;
    }

    public double[] getNodes() {
        return nodes;
    }

    @Override
    public Representation copy() {
        double[] newNodes = new double[nodes.length];
        for (int i = 0; i < newNodes.length; i++) {
            newNodes[i] = nodes[i];
        }
        return new RealVector(newNodes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(nodes);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof RealVector))
            return false;

        RealVector rv = (RealVector) obj;
        return Arrays.equals(nodes,rv.nodes);
    }

    @Override
    public String toString() {
        return Arrays.toString(nodes);
    }
}
