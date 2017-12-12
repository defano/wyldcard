package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.menu.HyperCardMenuBar;
import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.ContainerExp;
import com.defano.hypertalk.ast.containers.MenuContainerExp;
import com.defano.hypertalk.ast.containers.MenuItemContainerExp;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.expressions.*;
import com.defano.hypertalk.ast.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.specifiers.RemotePartSpecifier;
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
                new FactorAssociation<>(MenuItemContainerExp.class, this::deleteMenuItem),
                new FactorAssociation<>(MenuContainerExp.class, this::deleteMenu),
                new FactorAssociation<>(PartContainerExp.class, this::deletePart),
                new FactorAssociation<>(ContainerExp.class, this::deleteFromContainer)
        );

        if (!success) {
            throw new HtSemanticException("Can't delete that.");
        }
    }

    private void deletePart(PartContainerExp part) throws HtException {
        if (part.getChunk() != null) {
            deleteFromContainer(part);
        } else {

            try {
                PartSpecifier ps = part.evaluateAsSpecifier();
                PartModel p = ExecutionContext.getContext().getPart(ps);

                CardModel owner;
                if (ps instanceof RemotePartSpecifier) {
                    owner = HyperCard.getInstance().getStack().getStackModel().findRemotePartOwner((RemotePartSpecifier) ps);
                } else {
                    owner = ExecutionContext.getContext().getCurrentCard().getCardModel();
                }

                owner.removePartModel(p);
            } catch (PartException e) {
                throw new HtSemanticException("No such " + part.toString() + " to delete", e);
            }
        }
    }

    private void deleteMenuItem(MenuItemContainerExp menuItemExp) throws HtException {
        MenuItemSpecifier specifier = menuItemExp.item;
        specifier.getSpecifiedMenu().remove(specifier.getSpecifiedItemIndex());
    }

    private void deleteMenu(MenuContainerExp menuExp) throws HtException {
        HyperCardMenuBar.instance.deleteMenu(menuExp.menu.getSpecifiedMenu());
    }

    private void deleteFromContainer(ContainerExp container) throws HtException {
        container.putValue(new Value(), Preposition.INTO);
    }
}
