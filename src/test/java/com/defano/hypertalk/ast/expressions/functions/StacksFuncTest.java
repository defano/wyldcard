package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.stack.StackPart;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StacksFuncTest extends GuiceTest<StacksFunc> {

    private StackPart mockStackPart1 = Mockito.mock(StackPart.class, Mockito.RETURNS_DEEP_STUBS);
    private StackPart mockStackPart2 = Mockito.mock(StackPart.class, Mockito.RETURNS_DEEP_STUBS);

    @BeforeEach
    public void setUp() {
        initialize(new StacksFunc(mockParserRuleContext));
    }

    @Test
    public void testStacksFunc() throws HtException {
        // Setup
        final Value expectedResult = new Value("Stack 1\nStack 2");

        Mockito.when(mockStackPart1.getStackModel().getStackPath(mockExecutionContext)).thenReturn("Stack 1");
        Mockito.when(mockStackPart2.getStackModel().getStackPath(mockExecutionContext)).thenReturn("Stack 2");
        Mockito.when(WyldCard.getInstance().getStackManager().getOpenStacks()).thenReturn(Lists.newArrayList(mockStackPart1, mockStackPart2));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
