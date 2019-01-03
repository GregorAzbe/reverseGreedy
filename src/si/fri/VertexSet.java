package si.fri;

import java.util.AbstractCollection;

class VertexSet extends Solution<AbstractCollection<Integer>> {
    VertexSet(AbstractCollection<Integer> solution) {
        this.solution = solution;
    }

    @Override
    public int getQuality() {
        return solution.size();
    }
}
