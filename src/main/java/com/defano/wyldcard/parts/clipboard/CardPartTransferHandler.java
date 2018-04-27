package com.defano.wyldcard.parts.clipboard;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.PartToolContext;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.parts.*;
import com.defano.wyldcard.parts.card.CardLayer;
import com.defano.wyldcard.parts.card.CardLayerPart;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.hypertalk.ast.model.Value;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class CardPartTransferHandler extends TransferHandler {

    public CardPartTransferHandler(CardPart cardPart) {
        // Register canvas for cut/copy/paste actions
        ActionMap map = cardPart.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return TransferablePart.from(PartToolContext.getInstance().getSelectedPart());
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (action == MOVE) {
            PartToolContext.getInstance().deleteSelectedPart();
        }
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        try {
            ToolEditablePart part = (ToolEditablePart) info.getTransferable().getTransferData(TransferablePart.partFlavor);
            CardLayer layer = CardLayerPart.getActivePartLayer();
            ToolEditablePart importedPart = (ToolEditablePart) WyldCard.getInstance().getFocusedCard().importPart(new ExecutionContext(), part, layer);

            // Position pasted part over the mouse cursor
            importedPart.getPartModel().setKnownProperty(new ExecutionContext(), PartModel.PROP_LOC, new Value(MouseManager.getInstance().getMouseLoc()));

            SwingUtilities.invokeLater(() -> {
                // Make imported part selected
                ToolsContext.getInstance().forceToolSelection(importedPart.getEditTool(), false);
                PartToolContext.getInstance().setSelectedPart(importedPart);
            });

            return true;
        } catch (Throwable ignored) {
            // Nothing to do
        }

        return false;
    }

    private static class TransferablePart implements Transferable {

        private final static DataFlavor partFlavor = new DataFlavor(ToolEditablePart.class, DataFlavor.javaJVMLocalObjectMimeType);
        private final ToolEditablePart part;

        private TransferablePart(ToolEditablePart part) {
            this.part = part;
        }

        public static TransferablePart from(ToolEditablePart image) {
            return image == null ? null : new TransferablePart(image);
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{partFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == partFlavor;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor == partFlavor) {
                return part;
            }

            throw new UnsupportedFlavorException(flavor);
        }
    }

}
