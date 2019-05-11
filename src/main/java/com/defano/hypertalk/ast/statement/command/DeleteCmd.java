package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.factor.FactorAssociation;
import com.defano.hypertalk.ast.expression.container.ContainerExp;
import com.defano.hypertalk.ast.expression.container.MenuExp;
import com.defano.hypertalk.ast.expression.container.MenuItemExp;
import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.CompositePartSpecifier;
import com.defano.hypertalk.ast.model.specifier.MenuItemSpecifier;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.part.card.CardModel;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import org.antlr.v4.runtime.ParserRuleContext;

public class DeleteCmd extends Command {

    private final Expression expression;

    public DeleteCmd(ParserRuleContext context, Expression expression) {
        super(context, "delete");
        this.expression = expression;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        boolean success = expression.factor(
                context, new FactorAssociation<>(MenuItemExp.class, menuItemExp -> deleteMenuItem(context, menuItemExp)),
                new FactorAssociation<>(MenuExp.class, menuExp -> deleteMenu(context, menuExp)),
                new FactorAssociation<>(PartExp.class, part -> deletePart(context, part)),
                new FactorAssociation<>(ContainerExp.class, container -> deleteFromContainer(context, container))
        );

        if (!success) {
            throw new HtSemanticException("Can't delete that.");
        }
    }

    private void deletePart(ExecutionContext context, PartExp part) throws HtException {
        if (part.getChunk() != null) {
            deleteFromContainer(context, part);
        } else {

            try {
                PartSpecifier ps = part.evaluateAsSpecifier(context);
                PartModel p = context.getPart(ps);

                CardModel owner;
                if (ps instanceof CompositePartSpecifier) {
                    owner = context.getCurrentStack().getStackModel().findOwningCard(context, (CompositePartSpecifier) ps);
                } else {
                    owner = context.getCurrentCard().getPartModel();
                }

                owner.removePartModel(context, p);
            } catch (HtNoSuchPartException e) {
                throw new HtSemanticException("No such " + part.toString() + " to delete", e);
            }
        }
    }

    private void deleteMenuItem(ExecutionContext context, MenuItemExp menuItemExp) throws HtException {
        MenuItemSpecifier specifier = menuItemExp.item;
        Invoke.onDispatch(() -> specifier.getSpecifiedMenu(context).remove(specifier.getSpecifiedItemIndex(context)), HtException.class);
    }

    private void deleteMenu(ExecutionContext context, MenuExp menuExp) throws HtException {
        if (menuExp.getChunk() != null) {
            throw new HtSemanticException("Can't delete a chunk of a menu.");
        }

        WyldCard.getInstance().getWyldCardMenuBar().deleteMenu(menuExp.menu.getSpecifiedMenu(context));
    }

    private void deleteFromContainer(ExecutionContext context, ContainerExp container) throws HtException {
        container.putValue(context, new Value(), Preposition.REPLACING);
    }
}
