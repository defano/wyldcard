package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.lang.management.ManagementFactory;

public class TicksFunc extends Expression {

    public TicksFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
        long jvmStartTimeMs = ManagementFactory.getRuntimeMXBean().getUptime();

        // Ticks are 1/60th of a second...
        return new Value((long)(jvmStartTimeMs * .06));
    }
}
