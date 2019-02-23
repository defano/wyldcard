package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.exception.HtSemanticException;

import java.awt.event.KeyEvent;

public enum ArrowDirection {
    UP, DOWN, LEFT, RIGHT;

    public static ArrowDirection fromValue(Value value) throws HtSemanticException {
        for (ArrowDirection thisDirection : values()) {
            if (thisDirection.name().equalsIgnoreCase(value.toString())) {
                return thisDirection;
            }
        }

        throw new HtSemanticException("No such arrow direction: " + value);
    }

    public static ArrowDirection fromKeyEvent(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                return UP;
            case KeyEvent.VK_DOWN:
                return DOWN;
            case KeyEvent.VK_LEFT:
                return LEFT;
            case KeyEvent.VK_RIGHT:
                return RIGHT;
        }

        return null;
    }

    public int getKeyEvent() {
        switch (this) {
            case UP:
                return KeyEvent.VK_UP;
            case DOWN:
                return KeyEvent.VK_DOWN;
            case LEFT:
                return KeyEvent.VK_LEFT;
            case RIGHT:
                return KeyEvent.VK_RIGHT;
        }

        throw new IllegalStateException("Bug! Unimplemented direction.");
    }
}
