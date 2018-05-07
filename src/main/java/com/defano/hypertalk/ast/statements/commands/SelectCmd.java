package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.button.HyperCardButton;
import com.defano.wyldcard.parts.button.ButtonPart;
import com.defano.wyldcard.parts.button.styles.PopupButton;
import com.defano.wyldcard.parts.card.CardLayerPart;
import com.defano.wyldcard.parts.field.AddressableSelection;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.PartToolContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.wyldcard.window.WindowManager;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;
import com.defano.hypertalk.utils.RangeUtils;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectCmd extends Command {

    private Preposition preposition;
    private Expression expression;

    public SelectCmd(ParserRuleContext context, Expression expression) {
        this(context, null, expression);
    }

    public SelectCmd(ParserRuleContext context, Preposition preposition, Expression expression) {
        super(context, "select");
        this.preposition = preposition;
        this.expression = expression;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        PartExp containerExp = this.expression.factor(context, PartExp.class, new HtSemanticException("Cannot select text from that."));
        Chunk chunk = containerExp.getChunk();

        if (preposition == null && chunk == null) {
            selectPart(context);
        } else {
            preposition = preposition == null ? Preposition.INTO : preposition;
            selectText(context, containerExp, preposition, chunk);
        }
    }

    private void selectText(ExecutionContext context, PartExp containerExp, Preposition preposition, Chunk chunk) throws HtException {
        PartSpecifier specifier = containerExp.evaluateAsSpecifier(context);

        if (specifier.getType() != null && specifier.getType() == PartType.BUTTON) {
            selectMenuButtonItem(context, specifier, chunk);
        } else if (specifier.getType() == null || (specifier.getType() != PartType.FIELD && specifier.getType() != PartType.MESSAGE_BOX)) {
            throw new HtSemanticException("Expected a field here.");
        } else {
            selectManagedText(context, specifier, preposition, chunk);
        }
    }

    private void selectManagedText(ExecutionContext context, PartSpecifier specifier, Preposition preposition, Chunk chunk) throws HtException {
        PartModel partModel = context.getPart(specifier);

        if (!(partModel instanceof AddressableSelection)) {
            throw new IllegalStateException("Bug! Don't know how to select text in part: " + partModel);
        }

        AddressableSelection field = (AddressableSelection) partModel;

        Range range = chunk == null ?
                new Range(0, field.getSelectableText(context).length()) :                              // Entire contents
                (chunk instanceof CompositeChunk) ?
                        RangeUtils.getRange(context, field.getSelectableText(context), (CompositeChunk) chunk) : // Chunk of a chunk of contents
                        RangeUtils.getRange(context, field.getSelectableText(context), chunk);                  // Chunk of contents

        switch (preposition) {
            case BEFORE:
                field.setSelection(context, range.start, range.start);
                break;
            case AFTER:
                field.setSelection(context, range.end, range.end);
                break;
            default:
                field.setSelection(context, range.start, range.end);
                break;
        }
    }

    private void selectMenuButtonItem(ExecutionContext context, PartSpecifier specifier, Chunk chunk) throws HtException {
        if (chunk.type != ChunkType.LINE) {
            throw new HtSemanticException("Can't select the text of a button.");
        }

        PartModel partModel = context.getPart(specifier);
        ButtonPart part = (ButtonPart) context.getCurrentStack().getDisplayedCard().getPart(context, partModel);

        HyperCardButton component = (HyperCardButton) part.getButtonComponent();
        if (component instanceof PopupButton) {
            ((PopupButton) component).selectItem(chunk.start.evaluate(context).integerValue());
        } else {
            throw new HtSemanticException("Can't select lines of this type of button.");
        }
    }

    private void selectPart(ExecutionContext context) throws HtException {
        PartSpecifier specifier = this.expression.factor(context, PartExp.class, new HtSemanticException("Cannot select that.")).evaluateAsSpecifier(context);

        if (specifier.getType() == null || !specifier.isButtonOrFieldSpecifier()) {
            throw new HtSemanticException("Expected a button or field here.");
        }

        PartModel partModel = context.getPart(specifier);
        CardLayerPart part = context.getCurrentStack().getDisplayedCard().getPart(context, partModel);

        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            WindowManager.getInstance().getWindowForStack(context, context.getCurrentStack()).requestFocus();

            ToolsContext.getInstance().forceToolSelection(specifier.getType().getEditTool(), false);
            PartToolContext.getInstance().setSelectedPart((ToolEditablePart) part);
        });
    }
}
