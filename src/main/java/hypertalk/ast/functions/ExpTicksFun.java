package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;
import java.lang.management.ManagementFactory;

public class ExpTicksFun extends Expression implements Serializable {

    @Override
    public Value evaluate() throws HtSyntaxException {
        long jvmStartTimeMs = ManagementFactory.getRuntimeMXBean().getUptime();

        // Ticks are 1/60th of a second...
        return new Value((long)(jvmStartTimeMs * .06));
    }
}
