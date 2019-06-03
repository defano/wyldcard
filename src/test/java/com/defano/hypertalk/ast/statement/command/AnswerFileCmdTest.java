package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AnswerFileCmdTest extends GuiceTest<AnswerFileCmd> {

    private Expression mockPromptExpr = Mockito.mock(Expression.class);
    private Expression mockFilterExpr = Mockito.mock(Expression.class);

    @BeforeEach
    void setUp() {
        initialize(new AnswerFileCmd(mockParserRuleContext, mockPromptExpr, mockFilterExpr));
    }

    @Test
    void testThatSelectedFileIsReturned() throws HtException {
        // Setup
        Mockito.when(mockPromptExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The prompt"));
        Mockito.when(mockFilterExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The filter"));
        Mockito.when(mockDialogManager.answerFile(mockExecutionContext, new Value("The prompt"), new Value("The filter"))).thenReturn(new Value("The choice"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockExecutionContext).setIt(new Value("The choice"));
        Mockito.verify(mockExecutionContext).setResult(new Value());
    }

    @Test
    void testThatCancelledSelectionIsReturned() throws HtException {
        // Setup
        Mockito.when(mockPromptExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The prompt"));
        Mockito.when(mockFilterExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The filter"));
        Mockito.when(mockDialogManager.answerFile(mockExecutionContext, new Value("The prompt"), new Value("The filter"))).thenReturn(null);

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockExecutionContext).setIt(new Value());
        Mockito.verify(mockExecutionContext).setResult(new Value("Cancel"));
    }

}