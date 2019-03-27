package com.defano.wyldcard.parts.finder;

import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public interface StackPartFindingSpecifier {

    PartModel find(ExecutionContext context, StackPartFinder partFinder) throws PartException;
}
