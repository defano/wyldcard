package com.defano.hypercard.parts;

import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.specifiers.RemotePartSpecifier;
import com.defano.hypertalk.exception.HtException;

public interface StackPartContainer extends PartContainer {

    /**
     * {@inheritDoc}
     */
    @Override
    default PartModel findPart(PartSpecifier ps) throws PartException {
        if (ps instanceof RemotePartSpecifier) {
            return findRemotePart((RemotePartSpecifier) ps);
        } else {
            return PartContainer.super.findPart(ps);
        }
    }

    default PartModel findRemotePart(RemotePartSpecifier ps) throws PartException {
        return findRemotePartOwner(ps).findPart(ps.getRemotePartSpecifier());
    }

    default CardModel findRemotePartOwner(RemotePartSpecifier ps) throws PartException {
        try {
            return (CardModel) findPart(ps.getRemoteCardPartExp().evaluateAsSpecifier());
        } catch (HtException e) {
            throw new PartException("Can't find that card.");
        }
    }

}
