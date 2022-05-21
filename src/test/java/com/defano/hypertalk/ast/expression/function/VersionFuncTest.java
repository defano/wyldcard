package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VersionFuncTest extends GuiceTest<VersionFunc> {

    @BeforeEach
    public void setUp() {

        initialize(new VersionFunc(mockParserRuleContext));
    }

    @Test
    public void testTheVersion() {
        // Setup
        final Value expectedResult = new Value("2.41");

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
