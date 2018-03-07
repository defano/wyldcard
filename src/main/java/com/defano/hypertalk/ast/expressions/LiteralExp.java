package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.model.Value;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class LiteralExp extends Expression {

    public final String literal;

    public LiteralExp(ParserRuleContext context, Object literal) {
        super(context);
        this.literal = String.valueOf(literal);
    }

    public LiteralExp(ParserRuleContext context, List<Value> literals) {
        this(context, literals.toArray());
    }

    public LiteralExp(ParserRuleContext context, Object... literals) {
        super(context);

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < literals.length - 1; index++) {
            builder.append(literals[index]).append(",");
        }

        if (literals.length > 0) {
            builder.append(literals[literals.length - 1]);
        }

        this.literal = builder.toString();
    }
    
    public Value onEvaluate() {
        return new Value(literal);
    }

    public static LiteralExp ofCardinal(ParserRuleContext context, String cardinal) {
        switch (cardinal.toLowerCase()) {
            case "zero": return new LiteralExp(context, 0);
            case "one": return new LiteralExp(context, 1);
            case "two": return new LiteralExp(context, 2);
            case "three": return new LiteralExp(context, 3);
            case "four": return new LiteralExp(context, 4);
            case "five": return new LiteralExp(context, 5);
            case "six": return new LiteralExp(context, 6);
            case "seven": return new LiteralExp(context, 7);
            case "eight": return new LiteralExp(context, 8);
            case "nine": return new LiteralExp(context, 9);
            case "ten": return new LiteralExp(context, 10);

            default: throw new IllegalArgumentException("Bug! Unimplemented cardinal: " + cardinal);
        }
    }
}
