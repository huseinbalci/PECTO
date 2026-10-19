package representation;

import java.util.List;

public class BinaryPermutation implements Permutation<Boolean> {

    boolean[] nodes;

    public BinaryPermutation(boolean[] newNodes) {
        nodes = newNodes;
    }

    public BinaryPermutation(int size) {
        nodes = new boolean[size];
    }

    public BinaryPermutation(List<Boolean> nodeList) {
        nodes = new boolean[nodeList.size()];
        for (int i = 0; i < nodeList.size(); i++) {
            nodes[i] = nodeList.get(i);
        }
    }

    @Override
    public Boolean get(int index) {
        return nodes[index];
    }

    @Override
    public int size() {
        return nodes.length;
    }

    @Override
    public void swap(int i1, int i2) {
        nodes[i1] ^= nodes[i2];
        nodes[i2] ^= nodes[i1];
        nodes[i1] ^= nodes[i2];
    }

    @Override
    public Permutation<Boolean> copy() {
        boolean[] newNodes = new boolean[nodes.length];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        return new BinaryPermutation(newNodes);
    }

}
