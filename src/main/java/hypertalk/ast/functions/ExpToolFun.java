/*
 * ExpToolFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/18/17 12:53 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package hypertalk.ast.functions;

import hypercard.context.ToolsContext;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

public class ExpToolFun extends Expression {

    @Override
    public Value evaluate() throws HtSemanticException {
        return new Value(ToolsContext.getInstance().getSelectedTool().name().toLowerCase());
    }
}
