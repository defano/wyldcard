package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SelectedLocFuncTest extends GuiceTest<SelectedLocFunc> {

    @Mock
    private Value expectedPoint;

    @BeforeEach
    public void setup() {
        initialize(new SelectedLocFunc(mockParserRuleContext));
    }

    @Test
    public void testThatSelectedLocFuncReturnsLoc() throws HtException {
        Mockito.when(mockSelectionManager.getSelectedLoc()).thenReturn(expectedPoint);
        assertEquals(expectedPoint, uut.onEvaluate(mockExecutionContext));
    }

}