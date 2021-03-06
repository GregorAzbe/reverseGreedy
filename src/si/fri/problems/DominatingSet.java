package si.fri.problems;

import si.fri.*;
import si.fri.dataStructures.AdjacencyMatrixGraph;
import si.fri.dataStructures.IGraph;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DominatingSet implements IProblem {
    @Override
    public Solution greedy(IGraph g) {
        AdjacencyMatrixGraph graph = new AdjacencyMatrixGraph(g);

        AbstractCollection<Integer> dominantVertices = new ArrayList<>();

        Iterator<Integer> iterator = graph.getIterator(false);

        while (iterator.hasNext()) {
            int v = iterator.next();
            dominantVertices.add(v);
            graph.removeVertexAndNeighbours(v);
        }
        return new VertexSet(dominantVertices);
    }

    @Override
    public Solution reverseGreedy(IGraph g) {
        AdjacencyMatrixGraph graph = new AdjacencyMatrixGraph(g);
        AbstractCollection<Integer> dominantVertices = new ArrayList<>();
        int n = graph.getOriginalSize();
        int[] covCnt = new int[n], score = new int[n];
        for (int i = 0; i < n; i++) {
            covCnt[i] = graph.getVertexDeg(i) + 1;
            score[i] = graph.getVertexDeg(i) + 1;
        }
        for (int i = 0; i < n; i++)
            makeReverseGreedyIteration(graph, covCnt, score, dominantVertices);

        return new VertexSet(dominantVertices);
    }

    private void makeReverseGreedyIteration(AdjacencyMatrixGraph graph, int[] covCnt, int[] score, AbstractCollection<Integer> dominantVertices){
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

    @Override
    public boolean test(IGraph g, Solution dominantVertices){
        AdjacencyMatrixGraph testGraph = new AdjacencyMatrixGraph(g);
        boolean[] isCovered = new boolean[testGraph.getOriginalSize()];

        for (int v : ((VertexSet)dominantVertices).getSolution()){
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

    @Override
    public AdjacencyMatrixGraph generateGraph(int verticesNumber, int edgesNumber) {
        return new AdjacencyMatrixGraph(verticesNumber, edgesNumber);
    }

    @Override
    public String toString() {
        return "Dominantna množica";
    }
}
