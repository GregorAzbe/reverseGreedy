package si.fri;

public abstract class Solution<Type> {
    Type solution = null;

    abstract int getQuality();

    Solution() {
    }

    Type getSolution() {
        return solution;
    }
}
