/**
 * AbstractPartModel.java
 * @author matt.defano@gmail.com
 * 
 * Implements a table of model associated with a partSpecifier object. Provides
 * methods for defining, getting and setting model, as well as notifying
 * listeners of changes. 
 */

package hypercard.parts.model;

import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Value;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class AbstractPartModel extends PropertiesTable{

    public static final String PROP_SCRIPT = "script";
    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_LEFT = "left";
    public static final String PROP_TOP = "top";
    public static final String PROP_WIDTH = "width";
    public static final String PROP_HEIGHT = "height";

    private PartType type;

    protected AbstractPartModel(PartType type) {
        this.type = type;
    }

    public PartType getType () {
        return type;
    }

    public Rectangle getRect() {
        try {
            Rectangle rect = new Rectangle();
            rect.x = getProperty(ButtonModel.PROP_LEFT).integerValue();
            rect.y = getProperty(ButtonModel.PROP_TOP).integerValue();
            rect.height = getProperty(ButtonModel.PROP_HEIGHT).integerValue();
            rect.width = getProperty(ButtonModel.PROP_WIDTH).integerValue();

            return rect;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't get geometry for part model.");
        }
    }
}
