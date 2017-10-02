package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Convertible;
import com.defano.hypertalk.ast.containers.Container;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.DateUtils;

import java.util.Date;

public class ConvertCmd extends Command {

    private final Container container;
    private final Expression expression;
    private final Convertible from;
    private final Convertible to;

    public ConvertCmd (Container container, Convertible to) {
        this(container, null, to);
    }

    public ConvertCmd(Container container, Convertible from, Convertible to) {
        super("convert");

        this.container = container;
        this.expression = null;
        this.from = from;
        this.to = to;
    }

    public ConvertCmd(Expression expression, Convertible to) {
        this(expression, null, to);
    }

    public ConvertCmd(Expression expression, Convertible from, Convertible to) {
        super("convert");

        this.container = null;
        this.expression = expression;
        this.from = from;
        this.to = to;
    }

    @Override
    public void onExecute() throws HtException {
        Date timestamp = container == null ?
                DateUtils.dateOf(expression.evaluate(), from) :
                DateUtils.dateOf(container.getValue(), from);

        if (timestamp == null) {
            throw new HtSemanticException("Invalid date.");
        }

        if (container == null) {
            ExecutionContext.getContext().setIt(DateUtils.valueOf(timestamp, to));
        } else {
            container.putValue(DateUtils.valueOf(timestamp, to), Preposition.INTO);
        }
    }
}
