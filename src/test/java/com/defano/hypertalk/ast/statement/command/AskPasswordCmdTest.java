package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.window.DialogResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AskPasswordCmdTest extends GuiceTest<AskPasswordCmd> {

    private Expression mockPromptExpr = Mockito.mock(Expression.class);
    private Expression mockSuggestionExpr = Mockito.mock(Expression.class);

    @Test
    void testThatPasswordSuggestionIsDisplayed() throws HtException, Preemption {
        initialize(new AskPasswordCmd(mockParserRuleContext, true, mockPromptExpr, mockSuggestionExpr));

        Mockito.when(mockPromptExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The prompt"));
        Mockito.when(mockSuggestionExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The suggestion"));

        Mockito.when(mockDialogManager.askPassword(mockExecutionContext, new Value("The prompt"), new Value("The suggestion"), false))
                .thenReturn(new DialogResponse(new Value("button"), new Value("password")));

        uut.onExecute(mockExecutionContext);

        Mockito.verify(mockDialogManager).askPassword(mockExecutionContext, new Value("The prompt"), new Value("The suggestion"), false);
        Mockito.verify(mockExecutionContext).setIt(new Value("password"));
        Mockito.verify(mockExecutionContext).setResult(new Value("button"));
    }


    @Test
    void testThatPasswordIsReturnedInTheClear() throws HtException, Preemption {
        initialize(new AskPasswordCmd(mockParserRuleContext, true, mockPromptExpr));

        Mockito.when(mockPromptExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The prompt"));
        Mockito.when(mockDialogManager.askPassword(mockExecutionContext, new Value("The prompt"), new Value(), false))
                .thenReturn(new DialogResponse(new Value("button"), new Value("password")));

        uut.onExecute(mockExecutionContext);

        Mockito.verify(mockDialogManager).askPassword(mockExecutionContext, new Value("The prompt"), new Value(), false);
        Mockito.verify(mockExecutionContext).setIt(new Value("password"));
        Mockito.verify(mockExecutionContext).setResult(new Value("button"));
    }

    @Test
    void testThatPasswordIsReturnedHashed() throws HtException, Preemption {
        initialize(new AskPasswordCmd(mockParserRuleContext, false, mockPromptExpr));

        Mockito.when(mockPromptExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The prompt"));
        Mockito.when(mockDialogManager.askPassword(mockExecutionContext, new Value("The prompt"), new Value(), true))
                .thenReturn(new DialogResponse(new Value("button"), new Value("hashed-password")));

        uut.onExecute(mockExecutionContext);

        Mockito.verify(mockDialogManager).askPassword(mockExecutionContext, new Value("The prompt"), new Value(), true);
        Mockito.verify(mockExecutionContext).setIt(new Value("hashed-password"));
        Mockito.verify(mockExecutionContext).setResult(new Value("button"));
    }

}