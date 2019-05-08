package com.defano.wyldcard.parts.clipboard;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.button.ButtonPart;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.field.FieldPart;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.parts.*;
import com.defano.wyldcard.parts.card.CardLayerPart;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.serializer.Serializer;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

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
        return TransferablePart.from(WyldCard.getInstance().getPartToolManager().getSelectedPart());
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (action == MOVE) {
            WyldCard.getInstance().getPartToolManager().deleteSelectedPart();
        }
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        try {
            CardPart focusedCard = WyldCard.getInstance().getStackManager().getFocusedCard();

            ToolEditablePart copiedPart = (ToolEditablePart) info.getTransferable().getTransferData(TransferablePart.partFlavor);
            ToolEditablePart importedPart = duplicatePart(new ExecutionContext(), copiedPart, focusedCard);

            focusedCard.addNewPartToCard(new ExecutionContext(), importedPart);

            // Position pasted part over the mouse cursor
            Point mouseLoc = WyldCard.getInstance().getMouseManager().getMouseLoc(new ExecutionContext());
            if (focusedCard.getBounds().contains(mouseLoc)) {
                importedPart.getPartModel().set(new ExecutionContext(), PartModel.PROP_LOC, new Value(mouseLoc));
            }

            SwingUtilities.invokeLater(() -> {
                // Make imported part selected
                WyldCard.getInstance().getPaintManager().forceToolSelection(importedPart.getEditTool(), false);
                WyldCard.getInstance().getPartToolManager().setSelectedPart(importedPart);
            });

            return true;
        } catch (Throwable ignored) {
            // Nothing to do
        }

        return false;
    }

    private ToolEditablePart duplicatePart(ExecutionContext context, ToolEditablePart original, CardPart parentCard) {
        PartModel copiedPartModel = Serializer.copy(original.getPartModel());

        copiedPartModel.define(PartModel.PROP_ID).asConstant(getNewId(copiedPartModel, parentCard.getOwningStackModel(), parentCard.getPartModel()));
        copiedPartModel.setOwner(CardLayerPart.getActivePartLayer().asOwner());

        if (copiedPartModel instanceof ButtonModel) {
            return ButtonPart.fromModel(context, parentCard, (ButtonModel) copiedPartModel);
        } else if (copiedPartModel instanceof FieldModel) {
            return FieldPart.fromModel(context, parentCard, (FieldModel) copiedPartModel);
        }

        throw new IllegalStateException("Bug! Cannot duplicate this part type: " + original);
    }

    private Value getNewId(PartModel partModel, StackModel owningStack, CardModel owningCard) {
        if (partModel instanceof FieldModel) {
            return new Value(owningStack.getNextFieldId(owningCard.getId()));
        } else if (partModel instanceof ButtonModel) {
            return new Value(owningStack.getNextButtonId(owningCard.getId()));
        }

        throw new IllegalStateException("Bug! Not a supported type: " + partModel);
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
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (flavor == partFlavor) {
                return part;
            }

            throw new UnsupportedFlavorException(flavor);
        }
    }

}
