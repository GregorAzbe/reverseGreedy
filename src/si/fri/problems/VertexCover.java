package si.fri.problems;

import si.fri.*;
import si.fri.dataStructures.AdjacencyMatrixGraph;
import si.fri.dataStructures.IGraph;

import java.util.*;

public class VertexCover implements IProblem {
    @Override
    public Solution greedy(IGraph g) {
        AdjacencyMatrixGraph graph = new AdjacencyMatrixGraph(g);

        AbstractCollection<Integer> vertexCoverSet = new ArrayList<>();

        Iterator<Integer> iterator = graph.getIterator(false);

        while (iterator.hasNext()) {
            int v = iterator.next();
            if(graph.getVertexDeg(v) > 0) {
                vertexCoverSet.add(v);
                graph.removeVertex(v);
            }
        }
        return new VertexSet(vertexCoverSet);
    }

    @Override
    public Solution reverseGreedy(IGraph g) {
        AdjacencyMatrixGraph graph = new AdjacencyMatrixGraph(g);

        HashSet<Integer> vertexCoverSet = new HashSet<>();

        for (Iterator<Integer> iterator = graph.getIterator(true); iterator.hasNext(); ){
            int v = iterator.next();
            if(!vertexCoverSet.contains(v)) {
                vertexCoverSet.addAll(graph.getNeighbours(v));
            }
        }
        return new VertexSet(vertexCoverSet);
    }

    @Override
    public boolean test(IGraph g, Solution VertexCoverSet) {
        AdjacencyMatrixGraph testGraph = new AdjacencyMatrixGraph(g);
        for (int v : ((VertexSet) VertexCoverSet).getSolution()) {
            testGraph.removeVertex(v);
        }
        for(Iterator<Integer> iterator = testGraph.getIterator(true); iterator.hasNext();){
            if(testGraph.getVertexDeg(iterator.next()) > 0) return false;
        }
        return true;
    }

    @Override
    public AdjacencyMatrixGraph generateGraph(int verticesNumber, int edgesNumber) {
        return new AdjacencyMatrixGraph(verticesNumber, edgesNumber);
    }

    @Override
    public String toString() {
        return "Vozliščno pokritje";
    }
}