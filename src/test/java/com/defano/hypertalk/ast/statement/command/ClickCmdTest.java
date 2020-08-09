package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ClickCmdTest extends GuiceTest<ClickCmd> {

    private Expression clickLocExpr = Mockito.mock(Expression.class);
    private Expression modifierKeysExpr = Mockito.mock(Expression.class);

    @Test
    void testThatClickCmdClicksAtLocation() throws HtException {
        // Setup
        initialize(new ClickCmd(mockParserRuleContext, clickLocExpr, null));
        Mockito.when(clickLocExpr.evaluate(mockExecutionContext)).thenReturn(new Value("10, 100"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockMouseManager).clickAt(new Point(10, 100), false, false, false);
    }

    @Test
    void testThatClickCmdClicksAtLocationWithShiftKey() throws HtException {
        // Setup
        initialize(new ClickCmd(mockParserRuleContext, clickLocExpr, modifierKeysExpr));

        Mockito.when(clickLocExpr.evaluate(mockExecutionContext)).thenReturn(new Value("10, 100"));
        Mockito.when(modifierKeysExpr.evaluate(mockExecutionContext)).thenReturn(new Value("shiftKey"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockMouseManager).clickAt(new Point(10, 100), true, false, false);
    }

    @Test
    void testThatClickCmdClicksAtLocationWithOptionKey() throws HtException {
        // Setup
        initialize(new ClickCmd(mockParserRuleContext, clickLocExpr, modifierKeysExpr));

        Mockito.when(clickLocExpr.evaluate(mockExecutionContext)).thenReturn(new Value("10, 100"));
        Mockito.when(modifierKeysExpr.evaluate(mockExecutionContext)).thenReturn(new Value("optionKey"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockMouseManager).clickAt(new Point(10, 100), false, true, false);
    }

    @Test
    void testThatClickCmdClicksAtLocationWithCommandKey() throws HtException {
        // Setup
        initialize(new ClickCmd(mockParserRuleContext, clickLocExpr, modifierKeysExpr));

        Mockito.when(clickLocExpr.evaluate(mockExecutionContext)).thenReturn(new Value("10, 100"));
        Mockito.when(modifierKeysExpr.evaluate(mockExecutionContext)).thenReturn(new Value("commandKey"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockMouseManager).clickAt(new Point(10, 100), false, false, true);
    }

    @Test
    void testThatClickCmdClicksAtLocationWithCmdKey() throws HtException {
        // Setup
        initialize(new ClickCmd(mockParserRuleContext, clickLocExpr, modifierKeysExpr));

        Mockito.when(clickLocExpr.evaluate(mockExecutionContext)).thenReturn(new Value("10, 100"));
        Mockito.when(modifierKeysExpr.evaluate(mockExecutionContext)).thenReturn(new Value("cmdKey"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockMouseManager).clickAt(new Point(10, 100), false, false, true);
    }

    @Test
    void testThatClickCmdClicksAtLocationWithShiftOptionCmdKey() throws HtException {
        // Setup
        initialize(new ClickCmd(mockParserRuleContext, clickLocExpr, modifierKeysExpr));

        Mockito.when(clickLocExpr.evaluate(mockExecutionContext)).thenReturn(new Value("10, 100"));
        Mockito.when(modifierKeysExpr.evaluate(mockExecutionContext)).thenReturn(new Value("shiftKey, optionKey, cmdKey"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockMouseManager).clickAt(new Point(10, 100), true, true, true);
    }

}