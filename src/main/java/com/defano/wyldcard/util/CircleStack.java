package com.defano.wyldcard.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * An unusual data structure that behaves like a circular queue-based stack.
 */
public class CircleStack<T> {

    private final ArrayList<T> contents = new ArrayList<>();
    private final int maxDepth;
    private int pointer = 0;

    public CircleStack(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void push(T element) {
        if (contents.isEmpty() || !peekBack().equals(element)) {
            if (contents.size() >= maxDepth) {
                contents.remove((maxDepth - 1 - pointer) % maxDepth);
            }
            contents.add(pointer, element);
        }
    }

    public T pop() {
        return contents.remove(pointer);
    }

    public T back() {
        return contents.get(decrementPointer());
    }

    public T forward() {
        return contents.get(incrementPointer());
    }

    public T peekBack() {
        return contents.get(prevPointer());
    }

    public T peekForward() {
        return contents.get(nextPointer());
    }

    public Set<T> asSet() {
        return new HashSet<>(contents);
    }

    private int decrementPointer() {
        pointer = prevPointer();
        return pointer;
    }

    private int incrementPointer() {
        pointer = nextPointer();
        return pointer;
    }

    private int prevPointer() {
        return (pointer + 1) % contents.size();
    }

    private int nextPointer() {
        return (pointer - 1) % contents.size();
    }

}
