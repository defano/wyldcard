package com.defano.wyldcard.util;

import java.util.ArrayList;

public class LimitedDepthStack<T> {

    private final ArrayList<T> contents = new ArrayList<>();
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
