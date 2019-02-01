package si.fri.dataStructures;

import java.util.*;


public class AdjacencyListGraph implements IGraph {
    private List<Vertex> graph;

    AdjacencyListGraph(int nVertices, int nEdges) {
        graph = new ArrayList<>(nVertices);
        for (int i = 0; i < nVertices; i++) {
            graph.add(new Vertex(i));
        }

        Random random = new Random();
        for (int i = 0; i < nEdges; i++) {

            while (true) {
                Vertex u = graph.get(random.nextInt(nVertices));
                Vertex v = graph.get(random.nextInt(nVertices));
                if (u != v && !u.getNeighbours().contains(v)) {
                    u.addNeighbour(v);
                    v.addNeighbour(u);
                    break;
                }
            }

            /*
            Normal distribution of edges
            while (true) {
                int iU = -1;
                int iV = -1;
                while(iU < 0 || iU >= nVertices)
                    iU = (int) ((random.nextGaussian() / 5 + 0.5) * nVertices);
                while(iV < 0 || iV >= nVertices)
                    iV = (int) ((random.nextGaussian() / 5 + 0.5) * nVertices);

                iU  = (int)(iU/2.0);
                iV  = (int)(iV/2.0+nVertices/2.0);

                Vertex v = graph.get(iV);
                Vertex u = graph.get(iU);

                if (iU != iV && !u.getNeighbours().contains(v)) {
                    v.addNeighbour(u);
                    u.addNeighbour(v);
                    break;
                }
            }*/
        }
    }

    AdjacencyListGraph(IGraph graph) {
        this.graph = new ArrayList<>();
        this.graph = new ArrayList<>(((AdjacencyListGraph) graph).graph);
    }

    public int size() {
        return graph.size();
    }

    public Iterator<Vertex> getIterator(boolean isAscending) {
        AdjacencyListGraph graph = new AdjacencyListGraph(this);
        graph.graph.sort(Comparator.comparingInt(o -> (isAscending ? 1 : -1) * o.getNeighbours().size()));
        return graph.graph.iterator();
    }

    public List<Vertex> getNeighbours(int i) {
        return graph.get(i).getNeighbours();
    }

    /*void sort(Comparator<Vertex> comparator){
        graph.sort(comparator);
    }

    boolean isEmpty(){
        return graph.isEmpty();
    }*/

//    Vertex getFirstVertex(){
//        return graph.get(0);
//    }

    /*void removeNeighboursAndSelf(Vertex vertex){
        while (!vertex.getNeighbours().isEmpty()) {
            Vertex neighbour = vertex.getNeighbours().get(0);
            graph.remove(neighbour.remove());
        }
        graph.remove(vertex.remove());
    }

    int size(){
        return graph.size();
    }

    Vertex getMaxVertex(Comparator<Vertex> comparator){
        Vertex x = getFirstVertex();
        for (Vertex vertex : graph){
            if(comparator.compare(vertex, x) < 0){
                x = vertex;
            }
        }
        return x;
    }*/


    class Vertex {
        List<Vertex> getNeighbours() {
            return neighbours;
        }

        private List<Vertex> neighbours = new ArrayList<>();

        int score = 1, coverageCount = 1, index;

        Vertex(int index) {
            this.index = index;
        }

        void addNeighbour(Vertex vertex) {
            neighbours.add(vertex);
            score++;
            coverageCount++;
        }

        /*Vertex remove(){
            while (!neighbours.isEmpty()){
                Vertex neighbour = neighbours.get(0);
                neighbour.neighbours.remove(this);
                neighbours.remove(neighbour);
            }
            return this;
        }*/
    }
}

