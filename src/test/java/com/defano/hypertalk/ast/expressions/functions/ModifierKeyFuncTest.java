package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.enums.ModifierKey;
import com.defano.hypertalk.ast.model.Value;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModifierKeyFuncTest extends GuiceTest<ModifierKeyFunc> {

    @Test
    public void testModifierKeysUp() throws Exception {
        for (ModifierKey thisKey : ModifierKey.values()) {
            // Setup
            Value expectedResult = new Value("up");
            initialize(new ModifierKeyFunc(mockParserRuleContext, thisKey));

            Mockito.when(mockKeyboardManager.isCtrlCommandDown()).thenReturn(false);
            Mockito.when(mockKeyboardManager.isShiftDown()).thenReturn(false);
            Mockito.when(mockKeyboardManager.isAltOptionDown()).thenReturn(false);

            // Run the test
            final Value result = uut.onEvaluate(mockExecutionContext);

            // Verify the results
            assertEquals(expectedResult, result);
        }
    }

    @Test
    public void testCommandKeyDown() throws Exception {
        for (ModifierKey thisKey : ModifierKey.values()) {
            // Setup
            Value expectedResult = new Value("down");
            initialize(new ModifierKeyFunc(mockParserRuleContext, thisKey));

            Mockito.when(mockKeyboardManager.isCtrlCommandDown()).thenReturn(true);
            Mockito.when(mockKeyboardManager.isShiftDown()).thenReturn(true);
            Mockito.when(mockKeyboardManager.isAltOptionDown()).thenReturn(true);

            // Run the test
            final Value result = uut.onEvaluate(mockExecutionContext);

            // Verify the results
            assertEquals(expectedResult, result);
        }
    }

}
