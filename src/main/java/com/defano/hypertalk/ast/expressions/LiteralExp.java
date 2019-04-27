package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class LiteralExp extends Expression {

    public final Value literal;

    public LiteralExp(ParserRuleContext context, Object... literals) {
        super(context);

        if (literals.length == 1) {
            // maintain "isQuotedLiteral" property of value
            if (literals[0] instanceof Value) {
                this.literal = (Value) literals[0];
            } else {
                this.literal = new Value(stringValue(literals[0]));
            }
        }

        // Special case: Produce a comma-separated list of values
        else {
            StringBuilder builder = new StringBuilder();

            for (int index = 0; index < literals.length - 1; index++) {
                builder.append(stringValue(literals[index])).append(",");
            }

            if (literals.length > 0) {
                builder.append(stringValue(literals[literals.length - 1]));
            }

            this.literal = new Value(builder.toString());
        }
    }

    public static LiteralExp ofCardinal(ParserRuleContext context, String cardinal) {
        switch (cardinal.toLowerCase()) {
            case "zero":
                return new LiteralExp(context, 0);
            case "one":
                return new LiteralExp(context, 1);
            case "two":
                return new LiteralExp(context, 2);
            case "three":
                return new LiteralExp(context, 3);
            case "four":
                return new LiteralExp(context, 4);
            case "five":
                return new LiteralExp(context, 5);
            case "six":
                return new LiteralExp(context, 6);
            case "seven":
                return new LiteralExp(context, 7);
            case "eight":
                return new LiteralExp(context, 8);
            case "nine":
                return new LiteralExp(context, 9);
            case "ten":
                return new LiteralExp(context, 10);

            default:
                throw new IllegalArgumentException("Bug! Unimplemented cardinal: " + cardinal);
        }
    }

    private String stringValue(Object object) {
        if (object instanceof Value) {
            return object.toString();
        } else if (object instanceof LiteralExp) {
            return ((LiteralExp) object).literal.toString();
        } else {
            return String.valueOf(object);
        }
    }

    public Value onEvaluate(ExecutionContext context) {
        return literal;
    }
}
