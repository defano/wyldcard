package com.defano.wyldcard.parts.finder;

import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;

public interface OrderedPartFindingSpecifier {

    PartModel findSpecifiedPart(ExecutionContext context, List<PartModel> collection) throws PartException;
}
