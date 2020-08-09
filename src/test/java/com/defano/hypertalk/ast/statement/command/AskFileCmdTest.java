package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.window.DialogResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AskFileCmdTest extends GuiceTest<AskFileCmd> {

    private Expression mockPromptExpr = Mockito.mock(Expression.class);
    private DialogResponse mockDialogResponse = Mockito.mock(DialogResponse.class);

    @Test
    void testAskFileWithSelection() throws HtException {
        initialize(new AskFileCmd(mockParserRuleContext, mockPromptExpr));
        Mockito.when(mockPromptExpr.evaluate(mockExecutionContext)).thenReturn(new Value("The prompt"));
        Mockito.when(mockDialogManager.askFile(mockExecutionContext, new Value("The prompt"), null)).thenReturn(mockDialogResponse);
        Mockito.when(mockDialogResponse.getButtonResponse()).thenReturn(new Value("result"));
        Mockito.when(mockDialogResponse.getFieldResponse()).thenReturn(new Value("selection"));

        uut.onExecute(mockExecutionContext);

        Mockito.verify(mockDialogManager).askFile(mockExecutionContext, new Value("The prompt"), null);
        Mockito.verify(mockExecutionContext).setIt(new Value("selection"));
        Mockito.verify(mockExecutionContext).setResult(new Value("result"));
    }

}