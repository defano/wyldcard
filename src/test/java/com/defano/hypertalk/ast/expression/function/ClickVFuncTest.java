package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ClickVFuncTest extends GuiceTest<ClickVFunc> {

    @BeforeEach
    public void setup() {
        initialize(new ClickVFunc(mockParserRuleContext));
    }

    @Test
    public void testThatClickHReturnsValue() throws HtException {
        Point expectedPoint = new Point(10, 20);
        Mockito.when(mockMouseManager.getClickLoc()).thenReturn(expectedPoint);
        assertEquals(new Value(expectedPoint.y), uut.onEvaluate(mockExecutionContext));
    }
}