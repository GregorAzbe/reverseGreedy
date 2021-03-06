package si.fri.dataStructures;

import java.util.*;
import java.util.stream.IntStream;


public class AdjacencyMatrixGraph implements IGraph {
    private int[][] graph;
    private int[] deg;
    private boolean[] exists;
    private int n, nVertices;

    public AdjacencyMatrixGraph(int nVertices, int nEdges){
        setN(nVertices);

        graph = new int[n][n];
        deg = new int[n];
        exists = new boolean[n];

        Random random = new Random();

        for (int i = 0; i < nEdges; i++) {
            while(true) {
                int x = random.nextInt(n);
                int y = random.nextInt(n);
                if(graph[x][y] == 0 && x != y) {
                    graph[x][y] = 1;
                    graph[y][x] = 1;
                    deg[x]++;
                    deg[y]++;
                    break;
                }
            }
        }

        for (int i = 0; i < exists.length; i++) {
            exists[i] = true;
        }
    }


    public AdjacencyMatrixGraph(IGraph graph){
        AdjacencyMatrixGraph g = (AdjacencyMatrixGraph) graph;
        setN(g.n);
        this.deg = g.deg.clone();
        this.exists = g.exists.clone();

        this.graph = new int[n][n];
        for (int i = 0; i < nVertices; i++) {
            this.graph[i] = g.graph[i].clone();
        }
    }


    private void setN(int n) {
        this.n = n;
        this.nVertices = n;
    }

    public int getOriginalSize(){
        return n;
    }

    public int getVertexDeg(int i){
        return deg[i];
    }

    public List<Integer> getNeighbours(int index){
        List<Integer> neighbours = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if(graph[index][i] == 1){
                neighbours.add(i);
            }
        }
        return neighbours;
    }

    public void removeVertex(int index){
        for (int i = 0; i < n; i++) {
            if(graph[index][i] == 1){
                deg[i]--;
                graph[index][i] = 0;
                graph[i][index] = 0;
            }
        }
        exists[index] = false;
        deg[index] = 0;
        nVertices--;
    }

    public void removeVertexAndNeighbours(int index){
        for (int neighbour : getNeighbours(index)){
            removeVertex(neighbour);
        }
        removeVertex(index);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < graph.length; i++) {
            stringBuilder.append(i).append("| ");

            for (int j = 0; j < graph[i].length; j++) {
                stringBuilder.append(graph[i][j]).append(" ");
            }
            stringBuilder.append(String.format(" (%d) %c", deg[i],'\n'));
        }
        return stringBuilder.toString();
    }

    public Iterator<Integer> getIterator(boolean ascending) {
        int[] order = IntStream.range(0, n)
                .boxed().sorted(Comparator.comparingInt(i -> deg[i] * (ascending ? 1 : -1) ))
                .mapToInt(ele -> ele).toArray();

        return new Iterator<Integer>() {
            int i = 0;

            @Override

            public boolean hasNext() {
                for(int i = this.i; i < n; i++){
                    if(exists[order[i]]) return true;
                }
                return false;
            }

            @Override
            public Integer next() {
                Integer next = null;

                while(i < n){
                    if(exists[order[i]]){
                        next = order[i];
                        i++;
                        break;
                    }
                    i++;
                }
                return next;
            }
        };
    }
}