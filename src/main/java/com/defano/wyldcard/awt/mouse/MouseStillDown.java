package com.defano.wyldcard.awt.mouse;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.thread.Throttle;

import java.awt.event.MouseEvent;

/**
 * A synthesizer of mouseStillDown HyperCard system messages.
 */
public class MouseStillDown {

    private final static int MOUSE_STILL_DOWN_PERIOD_MS = 350;
    private final static Throttle mouseStillPressedThrottle = new Throttle("mouse-still-down-throttle", MOUSE_STILL_DOWN_PERIOD_MS);

    /**
     * Invoke when {@link java.awt.event.MouseListener#mousePressed(MouseEvent)} event occurs, and the given
     * {@link Runnable} will be fired if the mouse remains down after a given interval. Has no effect if the user
     * releases the mouse early.
     *
     * @param r The action to take if the mouse remains down.
     */
    public static void then (Runnable r) {
        mouseStillPressedThrottle.submitOnUiThread(() -> {
            if (WyldCard.getInstance().getMouseManager().isMouseDown()) {
                r.run();
            }
        });
    }
}