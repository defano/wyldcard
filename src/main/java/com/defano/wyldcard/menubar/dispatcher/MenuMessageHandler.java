package com.defano.wyldcard.menubar.dispatcher;

import com.defano.wyldcard.message.EvaluatedMessage;
import com.defano.wyldcard.message.MessageHandler;
import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.runtime.ExecutionContext;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

public class MenuMessageHandler implements MessageHandler {

    private final JMenuItem menuItem;
    private final List<ActionListener> actions;

    public MenuMessageHandler(JMenuItem menuItem, List<ActionListener> actionListeners) {
        this.menuItem = menuItem;
        this.actions = actionListeners;
    }

    @Override
    public boolean test(EvaluatedMessage m) {
        return m.getMessageName().equalsIgnoreCase(SystemMessage.DO_MENU.messageName) &&
                m.getArguments().size() == 1 &&
                m.getArguments().get(0).toString().equalsIgnoreCase(menuItem.getName());
    }

    public String getMenuItem() {
        return menuItem.getName();
    }

    @Override
    public void handleMessage(ExecutionContext context, EvaluatedMessage m) {
        for (ActionListener action : actions) {
            action.actionPerformed(null);
        }
    }

}
