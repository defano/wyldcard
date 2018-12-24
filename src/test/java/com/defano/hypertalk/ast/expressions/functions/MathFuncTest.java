package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.BuiltInFunction;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class MathFuncTest extends GuiceTest<MathFunc> {

    private Expression mockExpression = Mockito.mock(Expression.class);

    @Test
    public void testSqrt() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.sqrt(123.456));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.SQRT, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testSin() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.sin(123.456));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.SIN, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testCos() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.cos(123.456));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.COS, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testTan() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.tan(123.456));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.TAN, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testAtan() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.atan(123.456));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.ATAN, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.01);
    }

    @Test
    public void testExp() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.exp(123.456));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.EXP, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testExp1() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.expm1(123.456));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.EXP1, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testExp2() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.pow(2.0, 123.456));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.EXP2, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testLn() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.log(123.456));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.LN, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testLn1() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.log1p(123.456));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.LN1, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testLog2() throws Exception {
        // Setup
        final Value expectedResult = new Value(Math.log(123.456) / Math.log(2.0));

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.LOG2, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testTrunc() throws Exception {
        // Setup
        final Value expectedResult = new Value(123);

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.TRUNC, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(123.567));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testAbs() throws Exception {
        // Setup
        final Value expectedResult = new Value(123.456);

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.ABS, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(-123.456));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

    @Test
    public void testNumToChar() throws Exception {
        // Setup
        final Value expectedResult = new Value("A");

        initialize(new MathFunc(mockParserRuleContext, BuiltInFunction.NUM_TO_CHAR, mockExpression));
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(65));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
