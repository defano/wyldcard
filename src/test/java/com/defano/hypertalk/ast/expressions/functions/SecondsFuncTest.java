package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecondsFuncTest extends GuiceTest<SecondsFunc> {

    @BeforeEach
    public void setUp() {
        initialize();
    }

    @Test
    public void testOnEvaluate() throws HtException {
        assertEquals(System.currentTimeMillis() / 1000f, new SecondsFunc(mockParserRuleContext).evaluate(mockExecutionContext).longValue(), 10);
    }
}
