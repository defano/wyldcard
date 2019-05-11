package com.defano.wyldcard.part.finder;

import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.List;

/**
 * A part specifier that can identify a part within a collection of parts.
 */
public interface FindInCollectionSpecifier extends PartSpecifier {

    /**
     * Finds a specified part within an ordered collection of parts.
     *
     * @param context    The execution context
     * @param collection The collection of parts to look within
     * @return The specified part within the given collection
     * @throws HtNoSuchPartException Thrown if the specified part cannot be found
     */
    PartModel findInCollection(ExecutionContext context, List<PartModel> collection) throws HtNoSuchPartException;
}
