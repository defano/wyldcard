package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CloseCmdTest extends GuiceTest<CloseCmd> {

    private Expression mockFileExpr = Mockito.mock(Expression.class);

    @BeforeEach
    void setUp() {
        initialize(new CloseCmd(mockParserRuleContext, mockFileExpr));
    }

    @Test
    void testThatFileIsClosed() throws HtException {
        // Setup
        Mockito.when(mockFileExpr.evaluate(mockExecutionContext)).thenReturn(new Value("My File"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockFileManager).close("My File");
    }
}