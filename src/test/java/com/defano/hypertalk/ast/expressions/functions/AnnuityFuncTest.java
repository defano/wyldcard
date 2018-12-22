package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.common.collect.Lists;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

public class AnnuityFuncTest {

    @Mock
    private ParserRuleContext mockContext;
    @Mock
    private Expression mockExpressionList;
    @Mock
    private ExecutionContext mockExecutionContext;


    private AnnuityFunc annuityFuncUnderTest;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        annuityFuncUnderTest = new AnnuityFunc(mockContext, mockExpressionList);
    }

    @Test
    public void testWithRateAndPeriod() throws Exception {
        final Value expectedResult = new Value(0.1);

        Mockito.when(mockExpressionList.evaluateAsList(mockExecutionContext))
                .thenReturn(Lists.newArrayList(new Value(10), new Value(20)));

        // Run the test
        final Value result = annuityFuncUnderTest.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testWithIllegalRate() throws Exception {
        Mockito.when(mockExpressionList.evaluateAsList(mockExecutionContext))
                .thenReturn(Lists.newArrayList(new Value("barf"), new Value("10")));

        // Run the test
        assertThrows(HtSemanticException.class, () -> {
            annuityFuncUnderTest.onEvaluate(mockExecutionContext);
        });
    }

    @Test
    public void testWithIllegalPeriod() throws Exception {
        Mockito.when(mockExpressionList.evaluateAsList(mockExecutionContext))
                .thenReturn(Lists.newArrayList(new Value("10"), new Value("barf")));

        // Run the test
        assertThrows(HtSemanticException.class, () -> {
            annuityFuncUnderTest.onEvaluate(mockExecutionContext);
        });
    }

}
