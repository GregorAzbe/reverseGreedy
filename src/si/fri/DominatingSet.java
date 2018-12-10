package si.fri;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DominatingSet {
    public static List<Integer> greedy(Graph g) {
        Graph graph = new Graph(g);

        List<Integer> dominantVertices = new ArrayList<>();

        Iterator<Integer> iterator = graph.getIterator(false);

        while (iterator.hasNext()) {
            int v = iterator.next();
            dominantVertices.add(v);
            graph.removeVertexAndNeighbours(v);
        }
        return dominantVertices;
    }

    public static List<Integer> reverseGreedy(Graph g) {
        Graph graph = new Graph(g);

        List<Integer> dominantVertices = new ArrayList<>();

        int n = graph.getOriginalSize();
        int[] covCnt = new int[n], score = new int[n];

        for (int i = 0; i < n; i++) {
            covCnt[i] = graph.getVertexDeg(i) + 1;
            score[i] = graph.getVertexDeg(i) + 1;
        }

        for (int i = 0; i < n; i++) {
            int x = 0;
            int minScore = Integer.MAX_VALUE;

            for (int j = 0; j < score.length; j++) {
                if (score[j] < minScore){
                    minScore = score[j];
                    x = j;
                }
            }

            List<Integer> neighbours = graph.getNeighbours(x);
            neighbours.add(x);

            boolean isDominant = false;
            for (int neighbour : neighbours){
                if(covCnt[neighbour] == 1){
                    dominantVertices.add(x);

                    covCnt[x] = 0;
                    for (int neighbour1 : neighbours){
                        covCnt[neighbour1] = 0;
                    }
                    isDominant = true;
                    break;
                }
            }

            if(!isDominant){
                for (int neighbour : neighbours){
                    if(covCnt[neighbour] > 0){
                        covCnt[neighbour]--;
                        score[neighbour]++;
                    }
                }
            }
            score[x] = Integer.MAX_VALUE/2;
        }

        return dominantVertices;
    }

    public static boolean test(Graph g, List<Integer> dominantVertices){
        Graph testGraph = new Graph(g);
        boolean[] isCovered = new boolean[testGraph.getOriginalSize()];

        for (int v : dominantVertices){
            isCovered[v] = true;
            for(int n : testGraph.getNeighbours(v)){
                isCovered[n] = true;
            }
        }
        for (boolean x : isCovered){
            if(!x) return false;
        }
        return true;
    }
}
