package com.defano.wyldcard.menubar;

import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.message.MessageBuilder;
import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.thread.ThreadChecker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * This class represents an ActionListener that sends the "DoMenu" message to the current card before performing the
 * desired action. This allows the card, background or stack script to trap this behavior and override it as desired.
 */
public class DeferredMenuAction implements ActionListener {

    private final List<ActionListener> actionListeners;
    private final JMenuItem theMenu;
    private final JMenuItem theMenuItem;

    private CountDownLatch blocker;

    public DeferredMenuAction(JMenuItem menu, JMenuItem menuItem, List<ActionListener> actionListeners) {
        this.actionListeners = actionListeners;
        this.theMenu = menu;
        this.theMenuItem = menuItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionPerformed(ExecutionContext.unboundInstance(), e);
    }

    /**
     * Attempts to perform the requested action, if and only if the current card's message passing hierarchy does not
     * trap the 'doMenu' message. (This allows scripts to conditionally trap or override the behavior of a menu item).
     * <p>
     * Note that while this action "waits" for the message passing to occur, it does not block the current thread. Use
     * {@link #blockingInvokeActionPerformed(ExecutionContext, ActionEvent)} for situations when subsequent code is
     * dependent on the side effects of this action.
     *
     * @param e The ActionEvent to perform.
     */
    private void actionPerformed(ExecutionContext context, ActionEvent e) {

        final String[] menuName = new String[1];
        final String[] menuItemName = new String[1];

        Invoke.onDispatch(() -> {
            menuName[0] = theMenu.getText();
            menuItemName[0] = theMenuItem.getText();
        });

        // Attempts to invoke 'doMenu' handler which may require UI thread, thus, we have to wait on a background
        // thread while determining if 'doMenu' trapped menu handler.
        Invoke.asynchronouslyOnWorkerThread(() -> {

            CountDownLatch cdl = new CountDownLatch(1);
            final boolean[] trapped = new boolean[1];

            Message doMenuMessage = MessageBuilder
                    .named(SystemMessage.DO_MENU.messageName)
                    .withArgument(menuName[0])
                    .withArgument(menuItemName[0])
                    .build();

            context.getCurrentStack().getDisplayedCard().getPartModel().receiveMessage(context, doMenuMessage, (command, wasTrapped, err) -> {
                trapped[0] = wasTrapped;
                cdl.countDown();
            });

            try {
                cdl.await();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            if (!trapped[0]) {
                for (ActionListener thisAction : actionListeners) {
                    // Make sure that actions execute on the dispatch thread
                    Invoke.onDispatch(() -> thisAction.actionPerformed(e));
                }
            }

            if (blocker != null) {
                blocker.countDown();
            }
        });
    }

    /**
     * Performs the requested action, blocking the current thread until the action has been completed or trapped in
     * script.
     * <p>
     * This method cannot be executed on the Swing dispatch thread, as many scripts will require this thread in order to
     * complete.
     *
     * @param e The ActionEvent to perform.
     */
    public void blockingInvokeActionPerformed(ExecutionContext context, ActionEvent e) {
        ThreadChecker.assertWorkerThread();

        blocker = new CountDownLatch(1);
        actionPerformed(context, e);
        try {
            blocker.await();
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
        }
    }
}
