package hypercard.context;

import hypercard.parts.model.PropertiesModel;
import hypertalk.ast.common.Value;

public class GlobalProperties extends PropertiesModel {

    public final static String PROP_ITEMDELIMITER = "itemDelimiter";

    public GlobalProperties() {
        defineProperty(PROP_ITEMDELIMITER, new Value(","), false);
    }
}
