import java.util.Arrays;

public class BinaryTree<T extends Comparable<T>> {
    private BinaryTree<T> parent, left, right;
    private int index;
    private T data;

    public BinaryTree() {
        index = 0;
        data = null;
        parent = null;
        left = null;
        right = null;
    }

    @SafeVarargs
    public final void fill(T... data) {
        Arrays.sort(data);
        fill(0, data.length, data);
    }

    @SafeVarargs
    private final void fill(int from, int to, T... data) {
        this.index = 0;
        this.data = null;
        parent = null;
        left = null;
        right = null;
        switch (to - from) {
            case 0:
                return;
            case 1:
                index = from;
                this.data = data[index];
                return;
            default:
                index = (from + to) / 2;
                this.data = data[index];
                BinaryTree<T> left = new BinaryTree<T>(), right = new BinaryTree<T>();
                left.fill(from, index, data);
                right.fill(index + 1, to, data);
                if (from != index) append(left);
                if (to != index + 1) append(right);
        }
    }

    private void append(BinaryTree<T> child) {
        if (data.compareTo(child.data) > 0) {
            left = child;
            child.parent = this;
        } else if (data.compareTo(child.data) < 0) {
            right = child;
            child.parent = this;
        }
    }

    public T data() {
        return data;
    }

    public BinaryTree<T> left() {
        return left;
    }

    public BinaryTree<T> right() {
        return right;
    }

    public BinaryTree<T> parent() {
        return parent;
    }

    public int index() {
        return index;
    }

}
