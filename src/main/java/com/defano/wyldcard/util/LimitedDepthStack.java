package com.defano.wyldcard.util;

import java.util.ArrayList;
import java.util.List;

public class LimitedDepthStack<T> {

    private final ArrayList<T> contents = new ArrayList<>();
    private final int maxDepth;
    private int pointer = 0;

    public LimitedDepthStack(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void push(T element) {

        contents.add(element);

        if (contents.size() > maxDepth) {
            contents.remove(contents.size() - 1);
        }

        pointer = contents.size();
    }

    public T pop() {
        return contents.get(--pointer);
    }

    public boolean isEmpty() {
        return pointer == 0;
    }

    public List<T> asList() {
        return contents;
    }
}
