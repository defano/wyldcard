package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.expressions.VisualEffectExp;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.expressions.containers.VariableExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.commands.MessageCmd;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExpressionStatement extends Statement {

    public final Expression expression;
    
    public ExpressionStatement(ParserRuleContext context, Expression expression) {
        super(context);
        this.expression = expression;
    }
    
    public void onExecute(ExecutionContext context) throws HtException, Preemption {

        // Special case: A variable name used as a statement should be interpreted as a message command
        if (expression instanceof VariableExp) {
            MessageCmd messageCmd = new MessageCmd(super.getParserContext(), expression.evaluate(context).toString(), new ListExp(null));
            messageCmd.execute(context);
        }

        // Special case: A visual expression used as a statement locks the screen and sets the unlock effect to it
        VisualEffectExp visualEffectExp = expression.factor(context, VisualEffectExp.class);
        if (visualEffectExp != null) {
            context.getCurrentStack().getCurtainManager().lockScreen(context);
            context.setVisualEffect(visualEffectExp.factor(context, VisualEffectExp.class, new HtSemanticException("Not a visual effect.")).evaluateAsSpecifier(context));
        }

        Value v = expression.evaluate(context);
        context.setIt(v);
    }
}
