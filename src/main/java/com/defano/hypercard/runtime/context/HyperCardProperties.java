/*
 * GlobalProperties
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.runtime.context;

import com.defano.hypercard.fx.CurtainManager;
import com.defano.hypercard.sound.SoundPlayer;
import com.defano.hypercard.awt.MouseManager;
import com.defano.hypercard.parts.model.PropertiesModel;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.Value;

public class HyperCardProperties extends PropertiesModel {

    public final static String PROP_ITEMDELIMITER = "itemdelimiter";
    public final static String PROP_SELECTEDTEXT = "selectedtext";
    public final static String PROP_SELECTEDCHUNK = "selectedchunk";
    public final static String PROP_CLICKTEXT = "clicktext";
    public final static String PROP_LOCKSCREEN = "lockscreen";
    public final static String PROP_MOUSEH = "mouseh";
    public final static String PROP_MOUSEV = "mousev";
    public final static String PROP_SCREENRECT = "screenrect";
    public final static String PROP_CLICKLOC = "clickloc";
    public final static String PROP_CLICKH = "clickh";
    public final static String PROP_CLICKV = "clickv";
    public final static String PROP_SOUND = "sound";

    public HyperCardProperties() {
        super();

        defineProperty(PROP_ITEMDELIMITER, new Value(","), false);
        defineProperty(PROP_SELECTEDTEXT, new Value(""), true);
        defineProperty(PROP_LOCKSCREEN, new Value("false"), false);
        defineProperty(PROP_CLICKTEXT, new Value(""), true);
        defineProperty(PROP_MOUSEH, new Value(0), true);
        defineProperty(PROP_MOUSEV, new Value(0), true);
        defineProperty(PROP_SCREENRECT, new Value("0,0,0,0"), true);
        defineProperty(PROP_CLICKLOC, new Value("0, 0"), true);
        defineProperty(PROP_CLICKH, new Value("0"), true);
        defineProperty(PROP_CLICKV, new Value("0"), true);
        defineProperty(PROP_SOUND, new Value("done"), true);

        defineComputedGetterProperty(PROP_MOUSEH, (model, propertyName) -> new Value(MouseManager.getMouseLoc().x));
        defineComputedGetterProperty(PROP_MOUSEV, (model, propertyName) -> new Value(MouseManager.getMouseLoc().y));
        defineComputedGetterProperty(PROP_SCREENRECT, (model, propertyName) -> new Value(WindowManager.getStackWindow().getWindow().getGraphicsConfiguration().getBounds()));
        defineComputedGetterProperty(PROP_CLICKLOC, (model, propertyName) -> new Value(MouseManager.getClickLoc()));
        defineComputedGetterProperty(PROP_CLICKH, (model, propertyName) -> new Value(MouseManager.getClickLoc().x));
        defineComputedGetterProperty(PROP_CLICKV, (model, propertyName) -> new Value(MouseManager.getClickLoc().y));
        defineComputedGetterProperty(PROP_SOUND, (model, propertyName) -> new Value(SoundPlayer.getSound()));

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
