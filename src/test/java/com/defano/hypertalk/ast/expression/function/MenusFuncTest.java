package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MenusFuncTest extends GuiceTest<MenusFunc> {

    @BeforeEach
    public void setUp() {
        initialize(new MenusFunc(mockParserRuleContext));
    }

    @Test
    public void testOnEvaluate() {
        // Setup
        final Value expectedResult = new Value("Menu 0\nMenu 1\nMenu 2");
        Mockito.when(mockWyldCardMenuBar.getVisibleMenus()).thenReturn(Lists.newArrayList(
                new JMenu("Menu 0"), new JMenu("Menu 1"), new JMenu("Menu 2")
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
