package hypertalk.ast.containers;

import hypertalk.ast.expressions.ExpPart;

public class PropertySpecifier {

    public final String property;
    public final ExpPart partExp;

    public PropertySpecifier (String property, ExpPart partSpecifier) {
        this.property = property;
        this.partExp = partSpecifier;
    }
}
