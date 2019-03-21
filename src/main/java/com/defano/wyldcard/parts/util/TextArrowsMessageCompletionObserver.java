package com.defano.wyldcard.parts.util;

import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.interpreter.MessageCompletionObserver;
import com.defano.wyldcard.util.ThreadUtils;

import java.awt.event.KeyEvent;

/**
 * Listens to the result of sending 'keyDown' messages to the card message passing order; when not trapped, this class
 * provides textArrow navigation (navigate between cards by pressing the arrow keys).
 */
public class TextArrowsMessageCompletionObserver implements MessageCompletionObserver {

    private final KeyEvent e;
    private final CardPart cardPart;

    public TextArrowsMessageCompletionObserver(CardPart cardPart, KeyEvent e) {
        this.e = e;
        this.cardPart = cardPart;
    }

    @Override
    public void onMessagePassed(String message, boolean wasTrapped, HtException error) {

        if (error != null) {
            WyldCard.getInstance().showErrorDialog(error);
        }

        else if (!wasTrapped) {
            doArrowKeyNavigation();
        }
    }

    public void doArrowKeyNavigation() {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    WyldCard.getInstance().getNavigationManager().goPrevCard(new ExecutionContext(cardPart), cardPart.getOwningStack());
                    break;
                case KeyEvent.VK_RIGHT:
                    WyldCard.getInstance().getNavigationManager().goNextCard(new ExecutionContext(cardPart), cardPart.getOwningStack());
                    break;
            }
        });
    }
}
