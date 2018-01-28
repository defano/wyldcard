package com.defano.hypercard.parts.clipboard;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.card.CardPart;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CardActionListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        CardPart focusedCard = HyperCard.getInstance().getActiveStackDisplayedCard();

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
