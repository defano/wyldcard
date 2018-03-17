package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menu.HyperCardMenuBar;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.containers.ContainerExp;
import com.defano.hypertalk.ast.expressions.containers.MenuExp;
import com.defano.hypertalk.ast.expressions.containers.MenuItemExp;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.expressions.*;
import com.defano.hypertalk.ast.model.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DeleteCmd extends Command {

    private final Expression expression;

    public DeleteCmd(ParserRuleContext context, Expression expression) {
        super(context, "delete");
        this.expression = expression;
    }

    @Override
    protected void onExecute() throws HtException {
        boolean success = expression.factor(
                new FactorAssociation<>(MenuItemExp.class, this::deleteMenuItem),
                new FactorAssociation<>(MenuExp.class, this::deleteMenu),
                new FactorAssociation<>(PartExp.class, this::deletePart),
                new FactorAssociation<>(ContainerExp.class, this::deleteFromContainer)
        );

        if (!success) {
            throw new HtSemanticException("Can't delete that.");
        }
    }

    private void deletePart(PartExp part) throws HtException {
        if (part.getChunk() != null) {
            deleteFromContainer(part);
        } else {

            try {
                PartSpecifier ps = part.evaluateAsSpecifier();
                PartModel p = ExecutionContext.getContext().getPart(ps);

                CardModel owner;
                if (ps instanceof CompositePartSpecifier) {
                    owner = WyldCard.getInstance().getActiveStack().getStackModel().findOwningCard((CompositePartSpecifier) ps);
                } else {
                    owner = ExecutionContext.getContext().getCurrentCard().getCardModel();
                }

                owner.removePartModel(p);
            } catch (PartException e) {
                throw new HtSemanticException("No such " + part.toString() + " to delete", e);
            }
        }
    }

    private void deleteMenuItem(MenuItemExp menuItemExp) throws HtException {
        MenuItemSpecifier specifier = menuItemExp.item;
        specifier.getSpecifiedMenu().remove(specifier.getSpecifiedItemIndex());
    }

    private void deleteMenu(MenuExp menuExp) throws HtException {
        HyperCardMenuBar.getInstance().deleteMenu(menuExp.menu.getSpecifiedMenu());
    }

    private void deleteFromContainer(ContainerExp container) throws HtException {
        container.putValue(new Value(), Preposition.INTO);
    }
}
