package com.defano.hypercard.util;

import java.util.Vector;

public class LimitedDepthStack<T> {

    private final Vector<T> contents = new Vector<>();
    private final int maxDepth;

    public LimitedDepthStack(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void push (T element) {
        contents.add(0, element);
        if (contents.size() > maxDepth) {
            contents.remove(contents.size() - 1);
        }
    }

    public T pop() {
        return contents.remove(0);
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }
}
