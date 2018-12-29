package com.defano.wyldcard.cursor;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackNavigationObserver;
import com.defano.wyldcard.window.layouts.StackWindow;
import com.defano.hypertalk.ast.model.Value;
import com.defano.jmonet.tools.ArrowTool;
import com.defano.jmonet.tools.builder.PaintTool;
import com.google.inject.Singleton;

import javax.swing.*;
import java.awt.*;

/**
 * A singleton facade for managing the HyperCard cursor.
 */
@Singleton
public class DefaultCursorManager implements CursorManager {

    private HyperCardCursor activeCursor = HyperCardCursor.HAND;

    @Override
    public void start() {
        // Update cursor when the tool mode changes...
        WyldCard.getInstance().getToolsManager().getToolModeProvider().subscribe(toolMode -> updateCursor());

        // ... or when the focused stack changes
        WyldCard.getInstance().getStackManager().getFocusedStackProvider().subscribe(stackPart -> {
            updateCursor();

            // ... or when the card of the focused stack changes
            stackPart.addNavigationObserver(DefaultCursorManager.this);
        });

    }

    @Override
    public void setActiveCursor(HyperCardCursor cursor) {
        this.activeCursor = cursor;
        updateCursor();
    }

    @Override
    public void setActiveCursor(Value cursorName) {
        HyperCardCursor cursor = HyperCardCursor.fromHyperTalkName(cursorName);
        if (cursor != null) {
            setActiveCursor(cursor);
        }
    }

    @Override
    public HyperCardCursor getActiveCursor() {
        return activeCursor;
    }

    private void updateCursor() {
        ToolMode mode = WyldCard.getInstance().getToolsManager().getToolMode();
        PaintTool tool = WyldCard.getInstance().getToolsManager().getPaintTool();

        Cursor effectiveCursor = mode == ToolMode.BROWSE ?
                activeCursor.cursor :
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

        if (tool instanceof ArrowTool) {
            tool.setToolCursor(effectiveCursor);
        }

        SwingUtilities.invokeLater(() -> {
            StackWindow window = WyldCard.getInstance().getWindowManager().getFocusedStackWindow();
            if (window != null) {
                window.getDisplayedCard().setCursor(effectiveCursor);
                window.getScreenCurtain().setCursor(effectiveCursor);
            }
        });
    }

    @Override
    public void onCardOpened(CardPart newCard) {
        updateCursor();
    }
}
