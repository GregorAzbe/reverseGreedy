package si.fri;

import java.util.*;

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

        int[] solution = new int[graph.size()];
        int nColorsPrevious;
        DisjointSet disjointSet = new DisjointSet(graph.size());
        List<List<Integer>> colors = new ArrayList<>();
        for (int i = 0; i < graph.size(); i++) {
            solution[i] = i;
            ArrayList<Integer> color = new ArrayList<>();
            color.add(i);
            colors.add(color);
        }

        do {
            nColorsPrevious = disjointSet.getNumberOfSets();
            for (List<Integer> color1 : colors) {
                for (List<Integer> color2 : colors) {
                    if (color1 != color2 && !color1.isEmpty() && !color2.isEmpty()) {
                        int color1Element = color1.get(0), color2Element = color2.get(0);
                        if (!disjointSet.areInSameSet(color1Element, color2Element) && areColorsMergable(disjointSet, color1, color2, graph)) {
                            for (int v : color2) {
                                solution[v] = solution[color1Element];
                            }
                            disjointSet.mergeSets(color1Element, color2Element);
                            color1.addAll(color2);
                            color2.clear();

                        }
                    }
                }
            }
        } while (disjointSet.getNumberOfSets() < nColorsPrevious);

        return new Solution(solution, disjointSet.getNumberOfSets());
    }

    private boolean areColorsMergable(DisjointSet disjointSet, List<Integer> color1, List<Integer> color2, AdjacencyListGraph graph) {
        int v1 = color2.get(0);

        for (int v2 : color1) {
            for (AdjacencyListGraph.Vertex n : graph.getNeighbours(v2)) {
                if (disjointSet.areInSameSet(n.index, v1)) {
                    return false;
                }
            }
        }
        return true;
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
