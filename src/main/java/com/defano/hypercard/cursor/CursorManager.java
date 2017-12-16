package com.defano.hypercard.cursor;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.stack.StackObservable;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.jmonet.tools.ArrowTool;
import com.defano.jmonet.tools.base.PaintTool;

import java.awt.*;

public class CursorManager {

    private final static CursorManager instance = new CursorManager();
    private HyperCardCursor activeCursor = HyperCardCursor.HAND;

    private CursorManager() {
    }

    public static CursorManager getInstance() {
        return instance;
    }

    public void start() {
        // Update cursor when the tool mode changes...
        ToolsContext.getInstance().getToolModeProvider().addObserver((o, arg) -> updateCursor());

        // ... or when the card changes
        HyperCard.getInstance().getStack().addObserver(new StackObservable() {
            @Override
            public void onCardOpened(CardPart newCard) {
                updateCursor();
            }
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

        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            WindowManager.getStackWindow().getDisplayedCard().setCursor(effectiveCursor);
            WindowManager.getStackWindow().getScreenCurtain().setCursor(effectiveCursor);
        });
    }
}
