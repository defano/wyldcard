package com.defano.wyldcard.property.builder;

import com.defano.wyldcard.property.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * An object that is capable of supplying a {@link PropertiesModel}.
 */
public interface PropertiesModelProvider {
    /**
     * Gets a {@link PropertiesModel}.
     *
     * @param context The execution context.
     * @return The provided PropertiesModel.
     */
    PropertiesModel getPropertiesModel(ExecutionContext context);
}
