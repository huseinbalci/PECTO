package representation;

import java.util.Arrays;
import java.util.List;



public class IntegerPermutation implements Permutation<Integer>{
    int[] nodes;

    public static IntegerPermutation fromRoutes(List<List<Integer>> routes) {
        int[] nodes = routes.stream()
                .flatMap(List::stream)
                .mapToInt(x -> x)
                .toArray();
        return new IntegerPermutation(nodes);
    }

    public IntegerPermutation(List<Integer> nodeList) {
        nodes = nodeList.stream().mapToInt(x->x).toArray();
    }

    public IntegerPermutation(int[] newNodes) {
        nodes = newNodes;
    }

    public void overwrite(List<Integer> newNodes) {
        if (newNodes.size() != nodes.length) {
            nodes = newNodes.stream().mapToInt(x -> x).toArray();
        } else {
            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = newNodes.get(i);
            }
        }
    }

    @Override
    public Integer get(int index) {
        return nodes[index];
    }

    public void set(int index, int value) {
        nodes[index] = value;
    }

    @Override
    public int size() {
        return nodes.length;
    }

    @Override
    public void swap(int i1, int i2) {
        int tmp = nodes[i1];
        nodes[i1]= nodes[i2];
        nodes[i2]= tmp;
    }

    @Override
    public Permutation copy() {
        int[] newNodes = new int[nodes.length];


        for (int i = 0; i < nodes.length; i++) {
            newNodes[i]= nodes[i];
        }
        return new IntegerPermutation(newNodes);
    }

    @Override
    public String toString() {
        return Arrays.toString(nodes);
    }
}