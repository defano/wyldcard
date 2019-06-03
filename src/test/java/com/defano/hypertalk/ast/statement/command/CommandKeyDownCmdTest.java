package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.keyboard.ModifierKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CommandKeyDownCmdTest extends GuiceTest<CommandKeyDownCmd> {

    private Expression mockKeyExpr = Mockito.mock(Expression.class);

    @BeforeEach
    void setUp() {
        initialize(new CommandKeyDownCmd(mockParserRuleContext, mockKeyExpr));
    }

    @Test
    void onExecute() throws HtException {
        // Setup
        Mockito.when(mockKeyExpr.evaluate(mockExecutionContext)).thenReturn(new Value("string to type"));

        // Execute
        uut.onExecute(mockExecutionContext);

        Mockito.verify(mockRoboticTypist).type("string to type", ModifierKey.COMMAND);
    }
}