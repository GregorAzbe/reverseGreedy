package si.fri;

public abstract class Solution<Type> {
    protected Type solution = null;

    public abstract int getQuality();

    protected Solution() {
    }

    public Type getSolution() {
        return solution;
    }
}
