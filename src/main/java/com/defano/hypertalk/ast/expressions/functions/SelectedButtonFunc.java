package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.ButtonFamilySpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Collection;
import java.util.Optional;

public class SelectedButtonFunc extends Expression {

    private final ButtonFamilySpecifier familySpecifier;

    public SelectedButtonFunc(ParserRuleContext context, ButtonFamilySpecifier familySpecifier) {
        super(context);
        this.familySpecifier = familySpecifier;
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {

        Owner layer = familySpecifier.getLayer();
        int family = familySpecifier.getFamilyExpr().evaluate(context).integerValue();

        if (family < ButtonModel.MIN_BUTTON_FAMILY || family > ButtonModel.MAX_BUTTON_FAMILY) {
            throw new HtSemanticException("No such button family.");
        }

        Collection<ButtonModel> buttons = layer == Owner.CARD ?
                context.getCurrentCard().getPartModel().getButtonModels() :
                context.getCurrentCard().getPartModel().getBackgroundModel().getButtonModels();

        Optional<ButtonModel> foundButton = buttons.stream()
                .filter(b -> b.get(context, ButtonModel.PROP_FAMILY).integerValue() == family)
                .filter(b -> b.get(context, ButtonModel.PROP_HIGHLIGHT).booleanValue())
                .findFirst();

        return foundButton
                .map(buttonModel -> new Value(buttonModel.getLayerNumberHypertalkIdentifier(context)))
                .orElseGet(Value::new);
    }
}
