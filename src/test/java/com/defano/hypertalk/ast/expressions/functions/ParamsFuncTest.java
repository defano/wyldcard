package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParamsFuncTest extends GuiceTest<ParamsFunc> {

    @BeforeEach
    public void setUp() {
        initialize(new ParamsFunc(mockParserRuleContext));
    }

    @Test
    public void testMultipleParams() {
        // Setup
        final Value expectedResult = new Value("one, two, three");
        Mockito.when(mockExecutionContext.getStackFrame().getParams()).thenReturn(Lists.newArrayList(
                new Value("one"), new Value("two"), new Value("three")
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testSingleParams() {
        // Setup
        final Value expectedResult = new Value("one");
        Mockito.when(mockExecutionContext.getStackFrame().getParams()).thenReturn(Lists.newArrayList(
                new Value("one")
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testNoParams() {
        // Setup
        final Value expectedResult = new Value();
        Mockito.when(mockExecutionContext.getStackFrame().getParams()).thenReturn(Lists.newArrayList());

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
