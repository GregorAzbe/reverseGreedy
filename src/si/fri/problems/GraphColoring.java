package si.fri.problems;

import si.fri.dataStructures.AdjacencyListGraph;
import si.fri.dataStructures.DisjointSet;
import si.fri.dataStructures.IGraph;

import java.util.*;
import java.util.List;

public class GraphColoring implements IProblem {
    public Solution greedy(IGraph g) {
        AdjacencyListGraph graph = new AdjacencyListGraph(g);

        int[] colors = new int[graph.size()];
        int nColors = 0;

        Iterator<AdjacencyListGraph.Vertex> iterator = graph.getIterator(false);

        while (iterator.hasNext()) {
            int v = iterator.next().index, color = 1, oldColor = 0;
            while (color != oldColor) {
                oldColor = color;
                for (AdjacencyListGraph.Vertex n : graph.getNeighbours(v)) {
                    if (colors[n.index] == color) {
                        color++;
                    }
                }
            }
            colors[v] = color;
            nColors = Math.max(nColors, color);
        }
        return new Solution(colors, nColors);
    }

    public Solution reverseGreedy(IGraph g) {
        AdjacencyListGraph graph = new AdjacencyListGraph(g);
        DisjointSet disjointSet = new DisjointSet(graph.size());

        PriorityQueue<Color> colors = new PriorityQueue<>(graph.size(), Comparator.comparingInt(integers -> -integers.neighbours.size())); // descending
        for (int i = 0; i < graph.size(); i++) {
            colors.add(new Color(i, graph.getNeighbours(i)));
        }

        mergeColors(colors, disjointSet);

        int[] solution = new int[graph.size()];
        ArrayList<Integer> solutionColors = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++) {
            boolean colorAlreadyExists = false;
            for (int j = 0; j < solutionColors.size(); j++) {
                if (disjointSet.areInSameSet(i, solutionColors.get(j))) {
                    solution[i] = j + 1;
                    colorAlreadyExists = true;
                    break;
                }
            }
            if (!colorAlreadyExists) {
                solutionColors.add(i);
                solution[i] = solutionColors.size();
            }
        }

        return new Solution(solution, disjointSet.getNumberOfSets());
    }

    private void mergeColors(PriorityQueue<Color> colors1, DisjointSet disjointSet){
        while(colors1.size() > 1) {
            Color color1 = colors1.poll();
            PriorityQueue<Color> colors2 = new PriorityQueue<>(colors1.size(), Comparator.comparingInt(integers -> -integers.neighbours.size())); // descending
            colors2.addAll(colors1);

            while(!colors2.isEmpty()) {
                Color color2 = colors2.poll();

                int color1Element = color1.vertices.get(0), color2Element = color2.vertices.get(0);

                if(areColorsMergable(disjointSet, color1, color2)) {
                    disjointSet.mergeSets(color1Element, color2Element);
                    color2.vertices.addAll(color1.vertices);
                    color2.neighbours.addAll(color1.neighbours);

                    // Refresh priority queue
                    colors1.remove(color2);
                    colors1.add(color2);

                    break;
                }
            }
        }
    }

    private boolean areColorsMergable(DisjointSet disjointSet, Color color1, Color color2) {
        int v1 = color1.vertices.get(0);

        // You have to check just neighbours of one color
        for (AdjacencyListGraph.Vertex n : color2.neighbours) {
            if (disjointSet.areInSameSet(n.index, v1)) {
                return false;
            }
        }
        return true;
    }

    class Color {
        public List<Integer> vertices = new ArrayList<>();
        public List<AdjacencyListGraph.Vertex> neighbours;

        Color(int vertex, List<AdjacencyListGraph.Vertex> neighbours) {
            vertices.add(vertex);
            this.neighbours = neighbours;
        }
    }

    @Override
    public boolean test(IGraph g, si.fri.Solution solution) {
        int[] coloredGraph = ((Solution) solution).getSolution();
        Set<Integer> colors = new HashSet<>();

        for (int i = 0; i < coloredGraph.length; i++) {
            colors.add(coloredGraph[i]);
            for (AdjacencyListGraph.Vertex n : ((AdjacencyListGraph) g).getNeighbours(i))
                if (coloredGraph[i] == coloredGraph[n.index]) return false;
        }
        return colors.size() == solution.getQuality();
    }

    class Solution extends si.fri.Solution<int[]> {
        private int chromaticNumber;

        Solution(int[] vertexColors, int chromaticNumber) {
            this.solution = vertexColors;
            this.chromaticNumber = chromaticNumber;
        }

        @Override
        public int getQuality() {
            return chromaticNumber;
        }
    }

    @Override
    public AdjacencyListGraph generateGraph(int verticesNumber, int edgesNumber) {
        return new AdjacencyListGraph(verticesNumber, edgesNumber);
    }

    @Override
    public String toString() {
        return "Graph coloring";
    }
}
