package com.defano.wyldcard.part.clipboard;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.part.card.CardPart;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CardActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        CardPart focusedCard = WyldCard.getInstance().getStackManager().getFocusedCard();

        if (focusedCard != null) {
            Action a = focusedCard.getActionMap().get(e.getActionCommand());
            if (a != null) {
                try {
                    a.actionPerformed(new ActionEvent(focusedCard, ActionEvent.ACTION_PERFORMED, null));
                } catch (Throwable ignored) {
                    // Nothing to do
                }
            }
        }
    }

}
