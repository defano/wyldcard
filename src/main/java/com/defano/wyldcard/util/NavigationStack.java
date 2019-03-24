package com.defano.wyldcard.util;

import com.defano.hypertalk.ast.model.Destination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NavigationStack {

    private final ArrayList<Destination> contents = new ArrayList<>();
    private final int maxDepth;
    private int pointer = -1;

    public NavigationStack(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void push(Destination element) {
        if (contents.isEmpty() || !contents.get(pointer).equals(element)) {
            contents.add(++pointer, element);

            if (contents.size() > maxDepth) {
                contents.remove(0);
            }
        }
    }

    public Destination pop() {
        if (contents.isEmpty()) {
            return null;
        }

        return contents.remove(pointer--);
    }

    public Destination back() {
        pointer = prevPointer();
        return contents.get(pointer);
    }

    public Destination forward() {
        pointer = nextPointer();
        return contents.get(pointer);
    }

    public Destination peekBack() {
        return contents.get(prevPointer());
    }

    public Destination peekForward() {
        return contents.get(nextPointer());
    }

    public Set<Destination> asSet() {
        return new HashSet<>(contents);
    }

    private int prevPointer() {
        int prev = pointer - 1;
        if (prev < 0) {
            prev = contents.size() - 1;
        }
        return prev;
    }

    private int nextPointer() {
        int next = pointer + 1;
        if (next >= contents.size()) {
            next = 0;
        }
        return next;
    }

}
