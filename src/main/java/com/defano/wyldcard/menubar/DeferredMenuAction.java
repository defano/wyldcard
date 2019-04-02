package com.defano.wyldcard.menubar;

import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.message.MessageBuilder;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents an ActionListener that sends the "DoMenu" message to the current card before performing the
 * desired action. This allows the card, background or stack script to trap this behavior and override it as desired.
 */
public class DeferredMenuAction implements ActionListener {

    private final static ExecutorService delegatedActionExecutor = Executors.newSingleThreadExecutor();
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

        if (theMenuItem instanceof JCheckBoxMenuItem) {
            theMenuItem.setSelected(!theMenuItem.isSelected());
        }

        // Attempts to invoke 'doMenu' handler which may require UI thread, thus, we have to wait on a background
        // thread while determining if 'doMenu' trapped menu handler.
        delegatedActionExecutor.submit(() -> {

            CountDownLatch cdl = new CountDownLatch(1);
            final boolean[] trapped = new boolean[1];

            Message message = MessageBuilder
                    .named(SystemMessage.DO_MENU.messageName)
                    .withArgument(theMenu.getText())
                    .withArgument(theMenuItem.getText())
                    .build();

            context.getCurrentStack().getDisplayedCard().getPartModel().receiveMessage(context, message, (command, wasTrapped, err) -> {
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
                    ThreadUtils.invokeAndWaitAsNeeded(() -> thisAction.actionPerformed(e));
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
        ThreadUtils.assertWorkerThread();

        blocker = new CountDownLatch(1);
        actionPerformed(context, e);
        try {
            blocker.await();
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
        }
    }
}
