package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.card.CardLayerPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.PartToolContext;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class SelectPartCmd extends Command {

    private final PartExp part;

    public SelectPartCmd(PartExp part) {
        super("select");
        this.part = part;
    }

    @Override
    public void onExecute() throws HtException {
        PartSpecifier specifier = this.part.evaluateAsSpecifier();

        if (specifier.type() == null || (specifier.type() != PartType.FIELD && specifier.type() != PartType.BUTTON)) {
            throw new HtSemanticException("Expected a button or field here.");
        }

        PartModel partModel = HyperCard.getInstance().getDisplayedCard().findPart(specifier);
        CardLayerPart part = HyperCard.getInstance().getDisplayedCard().getPart(partModel);

        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            ToolsContext.getInstance().forceToolSelection(specifier.type().getEditTool(), false);
            PartToolContext.getInstance().setSelectedPart((ToolEditablePart) part);
        });
    }
}
