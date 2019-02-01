package si.fri;

import java.util.AbstractCollection;

public class VertexSet extends Solution<AbstractCollection<Integer>> {
    public VertexSet(AbstractCollection<Integer> solution) {
        this.solution = solution;
    }

    @Override
    public int getQuality() {
        return solution.size();
    }
}
