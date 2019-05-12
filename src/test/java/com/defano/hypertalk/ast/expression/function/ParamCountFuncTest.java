package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParamCountFuncTest extends GuiceTest<ParamCountFunc> {

    @BeforeEach
    public void setUp() {
        initialize(new ParamCountFunc(mockParserRuleContext));
    }

    @Test
    public void testMultipleParams() {
        // Setup
        final Value expectedResult = new Value(3);
        Mockito.when(mockExecutionContext.getStackFrame().getParams()).thenReturn(Lists.newArrayList(
                new Value("a"), new Value("b"), new Value("c")
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testSingleParam() {
        // Setup
        final Value expectedResult = new Value(1);
        Mockito.when(mockExecutionContext.getStackFrame().getParams()).thenReturn(Lists.newArrayList(
                new Value("a")
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testNoParams() {
        // Setup
        final Value expectedResult = new Value(0);
        Mockito.when(mockExecutionContext.getStackFrame().getParams()).thenReturn(Lists.newArrayList());

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
