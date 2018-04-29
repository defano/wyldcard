package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.finder.OrderedPartFinder;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.model.WindowProxyPartModel;
import com.defano.wyldcard.parts.msgbox.MsgBoxModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.window.WyldCardWindow;
import com.defano.wyldcard.window.WindowManager;
import org.antlr.v4.runtime.ParserRuleContext;

public class NumberOfPartFunc extends Expression {

    private final Expression partExpression;

    public NumberOfPartFunc(ParserRuleContext context, Expression partExpression) {
        super(context);
        this.partExpression = partExpression;
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        PartExp partFactor = partExpression.factor(context, PartExp.class, new HtSemanticException("Don't know how to get the number of that."));

        PartModel part = context.getPart(partFactor.evaluateAsSpecifier(context));

        if (part instanceof ButtonModel) {
            return new Value(((LayeredPartFinder) part.getParentPartModel()).getPartNumber(context, part, PartType.BUTTON));
        }

        if (part instanceof FieldModel) {
            return new Value(((LayeredPartFinder) part.getParentPartModel()).getPartNumber(context, part, PartType.FIELD));
        }

        if (part instanceof CardModel) {
            return new Value(((OrderedPartFinder) part.getParentPartModel()).getPartNumber(context, part, PartType.CARD));
        }

        if (part instanceof BackgroundModel) {
            return new Value(((OrderedPartFinder) part.getParentPartModel()).getPartNumber(context, part, PartType.BACKGROUND));
        }

        if (part instanceof MsgBoxModel) {
            return getNumberOfWindow(WindowManager.getInstance().getMessageWindow());
        }

        if (part instanceof WindowProxyPartModel) {
            return getNumberOfWindow(((WindowProxyPartModel) part).getWindow());
        }

        throw new HtSemanticException("Don't know how to get the number of that.");
    }

    private Value getNumberOfWindow(WyldCardWindow window) throws HtSemanticException {
        for (int windowNumber = 1; windowNumber <= WindowManager.getInstance().getWindows().size(); windowNumber++) {
            if (window == WindowManager.getInstance().getWindows().get(windowNumber - 1)) {
                return new Value(windowNumber);
            }
        }

        throw new HtSemanticException("Can't get the number of that window.");
    }
}
