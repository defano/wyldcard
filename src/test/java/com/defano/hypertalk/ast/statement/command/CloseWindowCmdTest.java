package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.WindowNameSpecifier;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.window.WyldCardFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class CloseWindowCmdTest extends GuiceTest<CloseWindowCmd> {

    private Expression mockWindowExpr = Mockito.mock(Expression.class);
    private WyldCardFrame mockFrame = Mockito.mock(WyldCardFrame.class);

    @BeforeEach
    void setUp() {
        initialize(new CloseWindowCmd(mockParserRuleContext, mockWindowExpr));
    }

    @Test
    void onExecute() throws HtException, Preemption {
        // Setup
        Mockito.when(mockWindowExpr.evaluate(mockExecutionContext)).thenReturn(new Value("My Window"));
        Mockito.when(mockWindowManager.findWindow(mockExecutionContext, new WindowNameSpecifier("My Window"))).thenReturn(mockFrame);

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockFrame).setVisible(false);
    }
}