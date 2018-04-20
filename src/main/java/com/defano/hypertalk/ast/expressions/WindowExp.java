package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.SingletonWindowType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.WindowNameSpecifier;
import com.defano.hypertalk.ast.model.specifiers.WindowTypeSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.HyperCardWindow;
import com.defano.wyldcard.window.WindowManager;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class WindowExp extends PartExp {

    private final SingletonWindowType type;
    private final Expression windowExpression;

    public WindowExp(ParserRuleContext context, Expression expression) {
        super(context);
        this.windowExpression = expression;
        this.type = null;
    }

    public WindowExp(ParserRuleContext context, SingletonWindowType type) {
        super(context);
        this.windowExpression = null;
        this.type = type;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) throws HtException {
        if (type != null) {
            return new WindowTypeSpecifier(type);
        } else {
            return new WindowNameSpecifier(windowExpression.evaluate(context).stringValue());
        }
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        return new Value(evaluateAsSpecifier(context).getHyperTalkIdentifier(context));
    }

    private HyperCardWindow evaluateAsWindow(ExecutionContext context) throws HtException {
        if (type != null) {
            return type.getWindow();
        } else {
            String windowName = windowExpression.evaluate(context).stringValue();
            List<HyperCardWindow> foundWindows = WindowManager.getInstance().getWindow(windowName);

            if (foundWindows.size() == 0) {
                throw new HtSemanticException("No such window.");
            } else {
                return foundWindows.get(0);
            }
        }
    }
}
