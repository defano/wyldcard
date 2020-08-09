package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.part.stack.StackPart;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class CloseCardWindowCmdTest extends GuiceTest<CloseCardWindowCmd> {

    private StackPart mockStackPart1 = Mockito.mock(StackPart.class);
    private StackPart mockStackPart2 = Mockito.mock(StackPart.class);

    @BeforeEach
    void setUp() {
        initialize(new CloseCardWindowCmd(mockParserRuleContext));
    }

    @Test
    void testThatCardWindowClosesWhenMultipleStacksOpen() throws HtException, Preemption {
        // Setup
        Mockito.when(mockStackManager.getOpenStacks()).thenReturn(Lists.newArrayList(mockStackPart1, mockStackPart2));
        Mockito.when(mockExecutionContext.getCurrentStack()).thenReturn(mockStackPart1);

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockStackManager).closeStack(mockExecutionContext, mockStackPart1);
    }

    @Test
    void testThatCardWindowDoesNotCloseWhenOnlyOneStackOpen() throws HtException, Preemption {
        // Setup
        Mockito.when(mockStackManager.getOpenStacks()).thenReturn(Lists.newArrayList(mockStackPart1));
        Mockito.when(mockExecutionContext.getCurrentStack()).thenReturn(mockStackPart1);

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockStackManager, Mockito.never()).closeStack(mockExecutionContext, mockStackPart1);
    }

}