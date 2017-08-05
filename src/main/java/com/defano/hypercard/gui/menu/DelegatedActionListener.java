package com.defano.hypercard.gui.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.SystemMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DelegatedActionListener implements ActionListener {

    private final List<ActionListener> actionListeners;
    private final String theMenu;
    private final String theMenuItem;

    public DelegatedActionListener(String theMenu, String theMenuItem, List<ActionListener> actionListeners) {
        this.actionListeners = actionListeners;
        this.theMenu = theMenu;
        this.theMenuItem = theMenuItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HyperCard.getInstance().getCard().getCardModel().sendMessage(SystemMessage.DO_MENU.messageName, new ExpressionList(theMenu, theMenuItem), (command, wasTrapped) -> {
            if (!wasTrapped) {
                for (ActionListener thisAction : actionListeners) {
                    thisAction.actionPerformed(e);
                }
            }
        });
    }
}
