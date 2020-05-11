package com.gr.five;

import java.util.Arrays;
import java.util.LinkedList;

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
                BinaryTree<T> left = new BinaryTree<>(), right = new BinaryTree<>();
                left.fill(from, index, data);
                right.fill(index + 1, to, data);
                if (from != index) append(left);
                if (to != index + 1) append(right);
        }
    }

    public void thread() {
        LinkedList<BinaryTree<T>> list = toListOfNodes();
        while (list.size() > 1) {
            BinaryTree<T> node = list.poll();
            if (node.right == null) node.right = list.peek();
            assert node.right != null;
            if (node.right.left == null) node.right.left = node;
        }
    }

    private LinkedList<BinaryTree<T>> toListOfNodes() {
        LinkedList<BinaryTree<T>> list = new LinkedList<>();
        if (left != null) list.addAll(left.toListOfNodes());
        list.add(this);
        if (right != null) list.addAll(right.toListOfNodes());
        return list;
    }

    public LinkedList<T> toList() {
        LinkedList<T> list = new LinkedList<>();
        if (left != null && left.parent == this) list.addAll(left.toList());
        list.add(data);
        if (right != null && right.parent == this) list.addAll(right.toList());
        return list;
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
