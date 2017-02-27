/*
 * GlobalProperties
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.context;

import com.defano.hypercard.parts.model.PropertiesModel;
import com.defano.hypertalk.ast.common.Value;

public class GlobalProperties extends PropertiesModel {

    public final static String PROP_ITEMDELIMITER = "itemDelimiter";
    public final static String PROP_SELECTEDTEXT = "selectedText";

    public GlobalProperties() {
        defineProperty(PROP_ITEMDELIMITER, new Value(","), false);
        defineProperty(PROP_SELECTEDTEXT, new Value(""), true);
    }
}
