package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VersionFuncTest extends GuiceTest<VersionFunc> {

    private Expression mockExpression = Mockito.mock(Expression.class);

    @BeforeEach
    public void setUp() {

        initialize(new VersionFunc(mockParserRuleContext));
    }

    @Test
    public void testNoArguments() throws Exception {
        // Setup
        final Value expectedResult = new Value("2.4.1");

        Mockito.when(mockExpression.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList());

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
