package com.defano.wyldcard.cursor;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackNavigationObserver;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.window.layouts.StackWindow;
import com.defano.wyldcard.window.WindowManager;
import com.defano.hypertalk.ast.model.Value;
import com.defano.jmonet.tools.ArrowTool;
import com.defano.jmonet.tools.builder.PaintTool;

import javax.swing.*;
import java.awt.*;

/**
 * A singleton facade for managing the HyperCard cursor.
 */
public class CursorManager implements StackNavigationObserver {

    private final static CursorManager instance = new CursorManager();
    private HyperCardCursor activeCursor = HyperCardCursor.HAND;

    private CursorManager() {}

    public static CursorManager getInstance() {
        return instance;
    }

    public void start() {
        // Update cursor when the tool mode changes...
        ToolsContext.getInstance().getToolModeProvider().subscribe(toolMode -> updateCursor());

        // ... or when the focused stack changes
        WyldCard.getInstance().getFocusedStackProvider().subscribe(stackPart -> {
            updateCursor();

            // ... or when the card of the focused stack changes
            stackPart.addNavigationObserver(CursorManager.this);
        });

    }

    public void setActiveCursor(HyperCardCursor cursor) {
        this.activeCursor = cursor;
        updateCursor();
    }

    public void setActiveCursor(Value cursorName) {
        HyperCardCursor cursor = HyperCardCursor.fromHyperTalkName(cursorName);
        if (cursor != null) {
            setActiveCursor(cursor);
        }
    }

    public HyperCardCursor getActiveCursor() {
        return activeCursor;
    }

    private void updateCursor() {
        ToolMode mode = ToolsContext.getInstance().getToolMode();
        PaintTool tool = ToolsContext.getInstance().getPaintTool();

        Cursor effectiveCursor = mode == ToolMode.BROWSE ?
                activeCursor.cursor :
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

        if (tool instanceof ArrowTool) {
            tool.setToolCursor(effectiveCursor);
        }

        SwingUtilities.invokeLater(() -> {
            StackWindow window = WindowManager.getInstance().getFocusedStackWindow();
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
