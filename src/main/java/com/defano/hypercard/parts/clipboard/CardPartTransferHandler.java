package com.defano.hypercard.parts.clipboard;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.context.PartToolContext;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.parts.*;

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
        return TransferablePart.from(PartToolContext.getInstance().getSelectedPartProvider().get());
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
            ToolEditablePart importedPart = (ToolEditablePart) HyperCard.getInstance().getCard().importPart(part, layer);

            SwingUtilities.invokeLater(() -> {
                // Make imported part selected
                ToolsContext.getInstance().setSelectedTool(importedPart.getEditTool());
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
