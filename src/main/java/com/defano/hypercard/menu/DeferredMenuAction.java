package com.defano.hypercard.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.model.ExpressionList;
import com.defano.hypertalk.ast.model.SystemMessage;

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

    private final static ExecutorService delegatedActionExecutor = Executors.newCachedThreadPool();

    private final List<ActionListener> actionListeners;
    private final String theMenu;
    private final String theMenuItem;

    public DeferredMenuAction(String theMenu, String theMenuItem, List<ActionListener> actionListeners) {
        this.actionListeners = actionListeners;
        this.theMenu = theMenu;
        this.theMenuItem = theMenuItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Attempts to invoke 'doMenu' handler which may require UI thread, thus, we have to wait on a background
        // thread while determining if 'doMenu' trapped menu handler.
        delegatedActionExecutor.submit(() -> {

            CountDownLatch cdl = new CountDownLatch(1);
            final boolean[] trapped = new boolean[1];

            HyperCard.getInstance().getActiveStackDisplayedCard().getCardModel().receiveMessage(SystemMessage.DO_MENU.messageName, new ExpressionList(null, theMenu, theMenuItem), (command, wasTrapped, err) -> {
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
        });
    }
}
