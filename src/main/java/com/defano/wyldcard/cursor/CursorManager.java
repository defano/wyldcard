package com.defano.wyldcard.cursor;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackNavigationObserver;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.window.StackWindow;
import com.defano.wyldcard.window.WindowManager;
import com.defano.hypertalk.ast.model.Value;
import com.defano.jmonet.tools.ArrowTool;
import com.defano.jmonet.tools.builder.PaintTool;

import javax.swing.*;
import java.awt.*;

/**
 * A singleton facade for managing the HyperCard cursor.
 */
public class CursorManager {

    private final static CursorManager instance = new CursorManager();
    private HyperCardCursor activeCursor = HyperCardCursor.HAND;

    private CursorManager() {}

    public static CursorManager getInstance() {
        return instance;
    }

    public void start() {
        // Update cursor when the tool mode changes...
        ToolsContext.getInstance().getToolModeProvider().subscribe(toolMode -> updateCursor());

        // ... or when the card changes
//        WyldCard.getInstance().getFocusedStack().addNavigationObserver(new StackNavigationObserver() {
//            @Override
//            public void onCardOpened(CardPart newCard) {
//                updateCursor();
//            }
//        });
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
}
