package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AnswerCmdTest extends GuiceTest<AnswerCmd> {

    private Expression mockMsgExpr = Mockito.mock(Expression.class);
    private Expression mockChoice1Expr = Mockito.mock(Expression.class);
    private Expression mockChoice2Expr = Mockito.mock(Expression.class);
    private Expression mockChoice3Expr = Mockito.mock(Expression.class);

    @Test
    void testDefaultChoiceAnswer() throws HtException {
        // Setup
        initialize(new AnswerCmd(mockParserRuleContext, mockMsgExpr));
        Mockito.when(mockMsgExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The message"));
        Mockito.when(mockDialogManager.answer(mockExecutionContext, new Value("The message"), new Value("OK"), null, null)).thenReturn(new Value("OK"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockExecutionContext).setIt(new Value("OK"));
    }

    @Test
    void testOneChoiceAnswer() throws HtException {
        // Setup
        initialize(new AnswerCmd(mockParserRuleContext, mockMsgExpr, mockChoice1Expr));
        Mockito.when(mockMsgExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The message"));
        Mockito.when(mockChoice1Expr.evaluate(mockExecutionContext)).thenReturn(new Value("Choice 1"));
        Mockito.when(mockDialogManager.answer(mockExecutionContext, new Value("The message"), new Value("Choice 1"), null, null)).thenReturn(new Value("Choice 1"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockExecutionContext).setIt(new Value("Choice 1"));

    }

    @Test
    void testTwoChoiceAnswer() throws HtException {
        // Setup
        initialize(new AnswerCmd(mockParserRuleContext, mockMsgExpr, mockChoice1Expr, mockChoice2Expr));
        Mockito.when(mockMsgExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The message"));
        Mockito.when(mockChoice1Expr.evaluate(mockExecutionContext)).thenReturn(new Value("Choice 1"));
        Mockito.when(mockChoice2Expr.evaluate(mockExecutionContext)).thenReturn(new Value("Choice 2"));
        Mockito.when(mockDialogManager.answer(mockExecutionContext, new Value("The message"), new Value("Choice 1"), new Value("Choice 2"), null)).thenReturn(new Value("Choice 2"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockExecutionContext).setIt(new Value("Choice 2"));
    }

    @Test
    void testThreeChoiceAnswer() throws HtException {
        // Setup
        initialize(new AnswerCmd(mockParserRuleContext, mockMsgExpr, mockChoice1Expr, mockChoice2Expr, mockChoice3Expr));
        Mockito.when(mockMsgExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The message"));
        Mockito.when(mockChoice1Expr.evaluate(mockExecutionContext)).thenReturn(new Value("Choice 1"));
        Mockito.when(mockChoice2Expr.evaluate(mockExecutionContext)).thenReturn(new Value("Choice 2"));
        Mockito.when(mockChoice3Expr.evaluate(mockExecutionContext)).thenReturn(new Value("Choice 3"));
        Mockito.when(mockDialogManager.answer(mockExecutionContext, new Value("The message"), new Value("Choice 1"), new Value("Choice 2"), new Value("Choice 3"))).thenReturn(new Value("Choice 3"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockExecutionContext).setIt(new Value("Choice 3"));
    }

}