package si.fri;

import java.util.List;

public class Main {
    private final static int VERTICES_N = 1000, EDGES_N = 10000;

    public static void main(String[] args) {
        Graph graph = new Graph(VERTICES_N, EDGES_N);

        System.out.println("Dominantna množica");
        System.out.println();
        System.out.println("Z požrešnim algoritmom:");

        List<Integer> dominantVertices = DominatingSet.greedy(graph);
        printResults(dominantVertices, graph);

        System.out.println();
        System.out.println("Z obratnim požrešnim algoritmom:");

        dominantVertices = DominatingSet.reverseGreedy(graph);
        printResults(dominantVertices, graph);
    }

    private static void printList(List<Integer> list){
        for(int l : list){
            System.out.print(l + " ");
        }
        System.out.println();
    }

    private static void printResults(List<Integer> dominantVertices, Graph graph){

        System.out.println(String.format("V dominantni množici je %d vozlišč (deluje %s)", dominantVertices.size(), DominatingSet.test(graph, dominantVertices) ? "pravilno" : "nepravilno"));
        System.out.println("Vozlišča v dominantni množici:");
        printList(dominantVertices);
    }
}
