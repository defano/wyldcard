package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.card.CardLayerPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.context.PartToolContext;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectPartCmd extends Command {

    private final PartContainerExp part;

    public SelectPartCmd(ParserRuleContext context, PartContainerExp part) {
        super(context, "select");
        this.part = part;
    }

    @Override
    public void onExecute() throws HtException {
        PartSpecifier specifier = this.part.evaluateAsSpecifier();

        if (specifier.getType() == null || (specifier.getType() != PartType.FIELD && specifier.getType() != PartType.BUTTON)) {
            throw new HtSemanticException("Expected a button or field here.");
        }

        PartModel partModel = ExecutionContext.getContext().getPart(specifier);
        CardLayerPart part = HyperCard.getInstance().getDisplayedCard().getPart(partModel);

        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            WindowManager.getStackWindow().requestFocus();

            ToolsContext.getInstance().forceToolSelection(specifier.getType().getEditTool(), false);
            PartToolContext.getInstance().setSelectedPart((ToolEditablePart) part);
        });
    }
}
