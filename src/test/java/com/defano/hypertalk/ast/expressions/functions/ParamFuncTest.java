package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParamFuncTest extends GuiceTest<ParamFunc> {

    private Expression theParamExp = Mockito.mock(Expression.class);

    @BeforeEach
    public void setUp() {
        initialize(new ParamFunc(mockParserRuleContext, theParamExp));
    }

    @Test
    public void testExistingParam() throws Exception {
        // Setup
        final Value expectedResult = new Value("two");
        Mockito.when(theParamExp.evaluate(mockExecutionContext)).thenReturn(new Value(2));
        Mockito.when(mockExecutionContext.getStackFrame().getParams()).thenReturn(Lists.newArrayList(
                new Value("one"), new Value("two"), new Value("three")
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testTheMessage() throws Exception {
        // Setup
        final Value expectedResult = new Value("the message");
        Mockito.when(theParamExp.evaluate(mockExecutionContext)).thenReturn(new Value(0));
        Mockito.when(mockExecutionContext.getStackFrame().getMessage()).thenReturn(expectedResult.toString());

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testBogusParam() throws Exception {
        // Setup
        final Value expectedResult = new Value();
        Mockito.when(theParamExp.evaluate(mockExecutionContext)).thenReturn(new Value(2378));
        Mockito.when(mockExecutionContext.getStackFrame().getParams()).thenReturn(Lists.newArrayList());

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
