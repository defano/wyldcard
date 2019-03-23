package com.defano.wyldcard.util;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.Direction;

import java.util.*;

public class NavigationStack {

    private final ArrayList<Destination> contents = new ArrayList<>();
    private final int maxDepth;
    private int pointer = 0;

    public NavigationStack(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void push(Destination element) {
        if (contents.isEmpty() || !contents.get(contents.size() - 1).equals(element)) {
            contents.add(element);

            if (contents.size() > maxDepth) {
                contents.remove(contents.size() - 1);
            }

            pointer = contents.size() - 1;
        }
    }

    public void adjust(Direction direction) {
        if (direction == null) {
            return;
        }

        switch (direction) {
            case BACK:
                pointer = prevPointer();
                break;
            case FORTH:
                pointer = nextPointer();
                break;
        }
    }

    public Destination peekBack() {
        Destination back = contents.get(prevPointer());
        back.setDirection(Direction.BACK);
        return back;
    }

    public Destination peekForward() {
        Destination forward = contents.get(nextPointer());
        forward.setDirection(Direction.FORTH);
        return forward;
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
