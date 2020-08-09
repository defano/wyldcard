package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CreateMenuCmdTest extends GuiceTest<CreateMenuCmd> {

    private Expression mockMenuNameExpr = Mockito.mock(Expression.class);

    @BeforeEach
    void setUp() {
        initialize(new CreateMenuCmd(mockParserRuleContext, mockMenuNameExpr));
    }

    @Test
    void onExecute() throws HtException {
        // Setup
        Mockito.when(mockMenuNameExpr.evaluate(mockExecutionContext)).thenReturn(new Value("Menu Name"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockWyldCardMenuBar).createMenu("Menu Name");
    }
}