package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
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
    protected void onExecute() throws HtException {
        PartExp containerExp = this.expression.factor(PartExp.class, new HtSemanticException("Cannot select text from that."));
        Chunk chunk = containerExp.getChunk();

        if (preposition == null && chunk == null) {
            selectPart();
        } else {
            preposition = preposition == null ? Preposition.INTO : preposition;
            selectText(containerExp, preposition, chunk);
        }
    }

    private void selectText(PartExp containerExp, Preposition preposition, Chunk chunk) throws HtException {
        PartSpecifier specifier = containerExp.evaluateAsSpecifier();

        if (specifier.getType() != null && specifier.getType() == PartType.BUTTON) {
            selectMenuButtonItem(specifier, chunk);
        } else if (specifier.getType() == null || (specifier.getType() != PartType.FIELD && specifier.getType() != PartType.MESSAGE_BOX)) {
            throw new HtSemanticException("Expected a field here.");
        } else {
            selectManagedText(specifier, preposition, chunk);
        }
    }

    private void selectManagedText(PartSpecifier specifier, Preposition preposition, Chunk chunk) throws HtException {
        PartModel partModel = ExecutionContext.getContext().getPart(specifier);

        if (!(partModel instanceof AddressableSelection)) {
            throw new IllegalStateException("Bug! Don't know how to select text in part: " + partModel);
        }

        AddressableSelection field = (AddressableSelection) partModel;

        Range range = chunk == null ?
                new Range(0, field.getSelectableText().length()) :                              // Entire contents
                (chunk instanceof CompositeChunk) ?
                        RangeUtils.getRange(field.getSelectableText(), (CompositeChunk) chunk) : // Chunk of a chunk of contents
                        RangeUtils.getRange(field.getSelectableText(), chunk);                  // Chunk of contents

        switch (preposition) {
            case BEFORE:
                field.setSelection(range.start, range.start);
                break;
            case AFTER:
                field.setSelection(range.end, range.end);
                break;
            default:
                field.setSelection(range.start, range.end);
                break;
        }
    }

    private void selectMenuButtonItem(PartSpecifier specifier, Chunk chunk) throws HtException {
        if (chunk.type != ChunkType.LINE) {
            throw new HtSemanticException("Can't select the text of a button.");
        }

        PartModel partModel = ExecutionContext.getContext().getPart(specifier);
        ButtonPart part = (ButtonPart) WyldCard.getInstance().getActiveStackDisplayedCard().getPart(partModel);

        HyperCardButton component = (HyperCardButton) part.getButtonComponent();
        if (component instanceof PopupButton) {
            ((PopupButton) component).selectItem(chunk.start.evaluate().integerValue());
        } else {
            throw new HtSemanticException("Can't select lines of this type of button.");
        }
    }

    private void selectPart() throws HtException {
        PartSpecifier specifier = this.expression.factor(PartExp.class, new HtSemanticException("Cannot select that.")).evaluateAsSpecifier();

        if (specifier.getType() == null || !specifier.isButtonOrFieldSpecifier()) {
            throw new HtSemanticException("Expected a button or field here.");
        }

        PartModel partModel = ExecutionContext.getContext().getPart(specifier);
        CardLayerPart part = WyldCard.getInstance().getActiveStackDisplayedCard().getPart(partModel);

        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            WindowManager.getInstance().getStackWindow().requestFocus();

            ToolsContext.getInstance().forceToolSelection(specifier.getType().getEditTool(), false);
            PartToolContext.getInstance().setSelectedPart((ToolEditablePart) part);
        });
    }
}
