package com.defano.wyldcard.menubar.dispatcher;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.WyldCardMenuItem;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.message.MessageBuilder;
import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.runtime.ExecutionContext;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WyldCardMenuAction implements ActionListener {

    private final JMenuItem item;

    public WyldCardMenuAction(JMenuItem menuItem) {
        this.item = menuItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Message message = MessageBuilder
                .named(SystemMessage.DO_MENU.messageName)
                .withArgument(item.getName()).build();

        if (item instanceof WyldCardMenuItem && ((WyldCardMenuItem) item).getMenuMessage() != null) {
            message = ((WyldCardMenuItem) item).getMenuMessage();
        }

        WyldCard.getInstance().getStackManager().getFocusedCard().getPartModel().receiveMessage(ExecutionContext.unboundInstance(), message);
    }
}
