package com.defano.wyldcard.parts;

import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.wyldcard.awt.DeferredKeyEvent;
import com.defano.wyldcard.message.Messagable;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.event.KeyEvent;

/**
 * An interface for components sending deferred key event messages.
 *
 * Swing requires a bit of event gymnastics in order to allow HyperTalk to conditionally trap key-press events.
 * Components requiring this behavior should implement this interface and use the
 * {@link Messagable#receiveAndDeferKeyEvent(ExecutionContext, String, ListExp, KeyEvent, DeferredKeyEventListener)}
 * method on the recipient part.
 */
public interface DeferredKeyEventListener {

    /**
     * Dispatch the event to underlying, message-receiving Swing component.
     * @param event The event to dispatch.
     */
    void dispatchEvent(DeferredKeyEvent event);
}
