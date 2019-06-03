package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.LiteralExp;
import com.defano.hypertalk.ast.expression.container.VariableExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DivideCmdTest extends GuiceTest<DivideCmd> {

    private Expression mockSourceExpr = Mockito.mock(Expression.class);
    private Expression mockContainerExpr = Mockito.mock(Expression.class);

    @BeforeEach
    public void setup() {
        initialize(new DivideCmd(mockParserRuleContext, mockSourceExpr, mockContainerExpr));
    }

    @Test
    public void testThatRValueCannotBeDivided() {
        // Setup
        initialize(new DivideCmd(mockParserRuleContext, mockSourceExpr, new LiteralExp(mockParserRuleContext)));

        // Execute
        assertThrows(HtSemanticException.class, () -> uut.onExecute(mockExecutionContext));
    }

    @Test
    public void testThatLValueCanBeDivided() throws HtException {
        // Setup
        initialize(new DivideCmd(mockParserRuleContext, mockSourceExpr, new VariableExp(mockParserRuleContext, "theVariable")));
        Mockito.when(mockExecutionContext.getVariable("theVariable")).thenReturn(new Value(10));
        Mockito.when(mockSourceExpr.evaluate(mockExecutionContext)).thenReturn(new Value(2));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockExecutionContext).setVariable("theVariable", Preposition.INTO, null, new Value(5));
    }

}