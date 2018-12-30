package si.fri;

public interface IProblem {
    Solution greedy(IGraph g);

    Solution reverseGreedy(IGraph g);

    boolean test(IGraph g, Solution dominantVertices);

    IGraph generateGraph(int verticesNumber, int edgesNumber);
}
