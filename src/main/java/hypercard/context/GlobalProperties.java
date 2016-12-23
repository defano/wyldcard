package hypercard.context;

import hypercard.parts.model.PropertiesTable;
import hypertalk.ast.common.Value;

public class GlobalProperties extends PropertiesTable {

    public final static String ITEM_DELIM_PROPERTY = "itemDelim";

    private final static String ITEM_DELIM_ALIAS = "itemDelimiter";

    public GlobalProperties() {
        super();

        // Define known, global HyperCard properties
        defineProperty(ITEM_DELIM_PROPERTY, new Value(","), false);
        defineAlias(ITEM_DELIM_PROPERTY, ITEM_DELIM_ALIAS);
    }
}
