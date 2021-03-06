package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.keyboard.ModifierKey;
import com.defano.wyldcard.awt.keyboard.RoboticTypist;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class TypeCmd extends Command {

    @Inject
    private RoboticTypist roboticTypist;

    public final Expression expression;
    public final Expression withModifierKeysExpr;

    public TypeCmd (ParserRuleContext context, Expression expression, Expression withModifierKeysExpr) {
        super(context, "type");

        this.expression = expression;
        this.withModifierKeysExpr = withModifierKeysExpr;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        String stringToType = expression.evaluate(context).toString();
        List<ModifierKey> modifierKeyList = new ArrayList<>();

        if (withModifierKeysExpr != null) {
            for (Value thisKey : withModifierKeysExpr.evaluate(context).getListItems()) {
                modifierKeyList.add(ModifierKey.fromHypertalkIdentifier(thisKey.toString()));
            }
        }
        
        roboticTypist.type(stringToType, modifierKeyList.toArray(new ModifierKey[0]));
    }
}
