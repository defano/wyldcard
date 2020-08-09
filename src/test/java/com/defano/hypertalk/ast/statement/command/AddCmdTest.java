package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.LiteralExp;
import com.defano.hypertalk.ast.expression.container.ContainerExp;
import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.*;

public class AddCmdTest extends GuiceTest<AddCmd> {

    private Expression mockSourceExpr = Mockito.mock(Expression.class);
    private Expression mockDestinationExpr = Mockito.mock(Expression.class);
    private ContainerExp mockContainerExpr = Mockito.mock(ContainerExp.class);

    @BeforeEach
    public void setUp() {
        initialize(new AddCmd(mockParserRuleContext, mockSourceExpr, mockDestinationExpr));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testThatLValueCanBeAddedTo() throws Exception {
        // Setup
        Mockito.when(mockSourceExpr.evaluate(mockExecutionContext)).thenReturn(new Value(10));
        Mockito.when(mockContainerExpr.evaluate(mockExecutionContext)).thenReturn(new Value(5));
        Mockito.when(mockDestinationExpr.factor((ExecutionContext) notNull(), (Class) notNull(), (HtException) notNull()))
                .thenReturn(mockContainerExpr);

        // Run the test
        uut.onExecute(mockExecutionContext);

        // Verify the results
        Mockito.verify(mockContainerExpr).putValue(mockExecutionContext, new Value(15), Preposition.INTO);
    }

    @Test
    public void testThatRValueCannotBeAdded() {
        // Setup
        initialize(new AddCmd(mockParserRuleContext, mockSourceExpr, new LiteralExp(mockParserRuleContext)));

        // Execute
        assertThrows(HtSemanticException.class, () -> uut.onExecute(mockExecutionContext));
    }

}
