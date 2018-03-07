package com.defano.hypercard.parts;

import com.defano.hypertalk.ast.expressions.Expression;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * An interface for components sending deferred key event messages.
 *
 * Swing requires a bit of event gymnastics in order to allow HyperTalk to conditionally trap key-press events.
 * Components requiring this behavior should implement this interface and use the
 * {@link Messagable#receiveAndDeferKeyEvent(String, Expression, KeyEvent, DeferredKeyEventComponent)} method
 * on the recipient part.
 */
public interface DeferredKeyEventComponent {
    /**
     * Invoked to inform the message-receiving component that a HyperTalk script is executing to determine if the
     * key event should be ignored by the underlying Swing component.
     *
     * Once this method is invoked with 'true', the message-receiving component should not invoke
     * {@link Messagable#receiveAndDeferKeyEvent(String, Expression, KeyEvent, DeferredKeyEventComponent)} until
     * it receives a subsequent call with false.
     *
     * @param redispatchInProcess True if a script is executing and the message recipient should ignore the event;
     *                            false otherwise.
     */
    void setPendingRedispatch(boolean redispatchInProcess);

    /**
     * Dispatch the event to underlying, message-receiving Swing component.
     * @param event The event to dispatch.
     */
    void dispatchEvent(AWTEvent event);
}
