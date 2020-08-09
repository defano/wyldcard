package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.enums.ToolType;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ChooseCmdTest extends GuiceTest<ChooseCmd> {

    private Expression mockToolExpr = Mockito.mock(Expression.class);

    @BeforeEach
    void setUp() {
        initialize(new ChooseCmd(mockParserRuleContext, mockToolExpr));
    }

    @Test
    void onExecute() throws HtException {

        for (ToolType thisToolType : ToolType.values()) {

            // Skip unnamed tools (like rotate, scale, etc.)
            if (!thisToolType.isHyperCardTool()) {
                continue;
            }

            // Setup
            Mockito.when(mockToolExpr.evaluate(mockExecutionContext)).thenReturn(new Value(thisToolType.getPrimaryToolName()));

            // Run the test
            uut.onExecute(mockExecutionContext);

            // Verify the results
            Mockito.verify(mockPaintManager).forceToolSelection(thisToolType, false);
        }
    }
}