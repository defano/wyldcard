package com.defano.hypercard.gui.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.SystemMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * This class represents an ActionListener that sends the "DoMenu" message to the current card before performing the
 * desired action. This allows the card, background or stack script to trap this behavior and override it as desired.
 */
public class DeferredMenuAction implements ActionListener {

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
        HyperCard.getInstance().getCard().getCardModel().receiveMessage(SystemMessage.DO_MENU.messageName, new ExpressionList(theMenu, theMenuItem), (command, wasTrapped, err) -> {

            if (!wasTrapped) {
                for (ActionListener thisAction : actionListeners) {

                    // Make sure that actions execute on the dispatch thread
                    SwingUtilities.invokeLater(() -> thisAction.actionPerformed(e));
                }
            }
        });
    }
}
