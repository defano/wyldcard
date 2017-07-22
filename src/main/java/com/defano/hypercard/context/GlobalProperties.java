/*
 * GlobalProperties
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.context;

import com.defano.hypercard.gui.fx.CurtainManager;
import com.defano.hypercard.parts.model.PropertiesModel;
import com.defano.hypertalk.ast.common.Value;

public class GlobalProperties extends PropertiesModel {

    public final static String PROP_ITEMDELIMITER = "itemdelimiter";
    public final static String PROP_SELECTEDTEXT = "selectedtext";
    public final static String PROP_LOCKSCREEN = "lockscreen";

    public GlobalProperties() {
        defineProperty(PROP_ITEMDELIMITER, new Value(","), false);
        defineProperty(PROP_SELECTEDTEXT, new Value(""), true);
        defineProperty(PROP_LOCKSCREEN, new Value("false"), false);

        addPropertyWillChangeObserver((property, oldValue, newValue) -> {
            switch (property.toLowerCase()) {
                case PROP_LOCKSCREEN:
                    CurtainManager.getInstance().setScreenLocked(newValue.booleanValue());
                    break;
            }
        });

    }

    public void resetProperties() {
        setKnownProperty(PROP_ITEMDELIMITER, new Value(","));
        setKnownProperty(PROP_LOCKSCREEN, new Value("false"));
    }
}
