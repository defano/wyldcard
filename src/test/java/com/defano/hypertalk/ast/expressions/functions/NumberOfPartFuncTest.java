package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.model.WindowProxyPartModel;
import com.defano.wyldcard.parts.msgbox.MsgBoxModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.window.WyldCardFrame;
import com.defano.wyldcard.window.layouts.MessageWindow;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NumberOfPartFuncTest extends GuiceTest<NumberOfPartFunc> {

    private Expression partArgument = Mockito.mock(Expression.class);
    private PartExp mockPartExp = Mockito.mock(PartExp.class);
    private PartSpecifier mockPartSpec = Mockito.mock(PartSpecifier.class);

    @BeforeEach
    public void setUp() {
        initialize(new NumberOfPartFunc(mockParserRuleContext, partArgument));
    }

    @Test
    public void testNumberOfWindow() throws HtException {
        Value expectedValue = new Value(4);
        WyldCardFrame mockFrame = Mockito.mock(WyldCardFrame.class);
        WindowProxyPartModel mockWindowProxy = Mockito.mock(WindowProxyPartModel.class);

        Mockito.when(mockWindowManager.getFrames(Mockito.anyBoolean())).thenReturn(Lists.newArrayList(
                Mockito.mock(WyldCardFrame.class), Mockito.mock(WyldCardFrame.class), Mockito.mock(WyldCardFrame.class), mockFrame
        ));

        Mockito.when(mockWindowProxy.getWindow()).thenReturn(mockFrame);
        Mockito.when(partArgument.factor(Mockito.eq(mockExecutionContext), Mockito.eq(PartExp.class), Mockito.any(HtSemanticException.class))).thenReturn(mockPartExp);
        Mockito.when(mockPartExp.evaluateAsSpecifier(mockExecutionContext)).thenReturn(mockPartSpec);
        Mockito.when(mockExecutionContext.getPart(mockPartSpec)).thenReturn(mockWindowProxy);

        assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testNumberOfMsgWindow() throws HtException {
        Value expectedValue = new Value(2);
        MessageWindow msgWindow = Mockito.mock(MessageWindow.class);
        PartModel mockPartModel = Mockito.mock(MsgBoxModel.class);

        Mockito.when(mockWindowManager.getFrames(Mockito.anyBoolean())).thenReturn(Lists.newArrayList(
                Mockito.mock(WyldCardFrame.class), msgWindow, Mockito.mock(WyldCardFrame.class), Mockito.mock(WyldCardFrame.class)
        ));

        Mockito.when(mockWindowManager.getMessageWindow()).thenReturn(msgWindow);
        Mockito.when(partArgument.factor(Mockito.eq(mockExecutionContext), Mockito.eq(PartExp.class), Mockito.any(HtSemanticException.class))).thenReturn(mockPartExp);
        Mockito.when(mockPartExp.evaluateAsSpecifier(mockExecutionContext)).thenReturn(mockPartSpec);
        Mockito.when(mockExecutionContext.getPart(mockPartSpec)).thenReturn(mockPartModel);

        assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testNumberOfBkgnd() throws HtException {
        Value expectedValue = new Value(10L);
        PartModel mockPartModel = Mockito.mock(BackgroundModel.class);
        StackModel mockParentModel = Mockito.mock(StackModel.class);

        Mockito.when(partArgument.factor(Mockito.eq(mockExecutionContext), Mockito.eq(PartExp.class), Mockito.any(HtSemanticException.class))).thenReturn(mockPartExp);
        Mockito.when(mockPartExp.evaluateAsSpecifier(mockExecutionContext)).thenReturn(mockPartSpec);
        Mockito.when(mockExecutionContext.getPart(mockPartSpec)).thenReturn(mockPartModel);
        Mockito.when(mockPartModel.getParentPartModel()).thenReturn(mockParentModel);
        Mockito.when(mockParentModel.getPartNumber(mockExecutionContext, mockPartModel, PartType.BACKGROUND)).thenReturn(expectedValue.longValue());

        assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testNumberOfCard() throws HtException {
        Value expectedValue = new Value(10L);
        PartModel mockPartModel = Mockito.mock(CardModel.class);
        StackModel mockParentModel = Mockito.mock(StackModel.class);

        Mockito.when(partArgument.factor(Mockito.eq(mockExecutionContext), Mockito.eq(PartExp.class), Mockito.any(HtSemanticException.class))).thenReturn(mockPartExp);
        Mockito.when(mockPartExp.evaluateAsSpecifier(mockExecutionContext)).thenReturn(mockPartSpec);
        Mockito.when(mockExecutionContext.getPart(mockPartSpec)).thenReturn(mockPartModel);
        Mockito.when(mockPartModel.getParentPartModel()).thenReturn(mockParentModel);
        Mockito.when(mockParentModel.getPartNumber(mockExecutionContext, mockPartModel, PartType.CARD)).thenReturn(expectedValue.longValue());

        assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testNumberOfCardField() throws HtException {
        Value expectedValue = new Value(123L);
        PartModel mockPartModel = Mockito.mock(FieldModel.class);
        CardModel mockParentModel = Mockito.mock(CardModel.class);

        Mockito.when(partArgument.factor(Mockito.eq(mockExecutionContext), Mockito.eq(PartExp.class), Mockito.any(HtSemanticException.class))).thenReturn(mockPartExp);
        Mockito.when(mockPartExp.evaluateAsSpecifier(mockExecutionContext)).thenReturn(mockPartSpec);
        Mockito.when(mockExecutionContext.getPart(mockPartSpec)).thenReturn(mockPartModel);
        Mockito.when(mockPartModel.getParentPartModel()).thenReturn(mockParentModel);
        Mockito.when(mockParentModel.getPartNumber(mockExecutionContext, mockPartModel, PartType.FIELD)).thenReturn(expectedValue.longValue());

        assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testNumberOfBkgndField() throws HtException {
        Value expectedValue = new Value(123L);
        PartModel mockPartModel = Mockito.mock(FieldModel.class);
        BackgroundModel mockParentModel = Mockito.mock(BackgroundModel.class);

        Mockito.when(partArgument.factor(Mockito.eq(mockExecutionContext), Mockito.eq(PartExp.class), Mockito.any(HtSemanticException.class))).thenReturn(mockPartExp);
        Mockito.when(mockPartExp.evaluateAsSpecifier(mockExecutionContext)).thenReturn(mockPartSpec);
        Mockito.when(mockExecutionContext.getPart(mockPartSpec)).thenReturn(mockPartModel);
        Mockito.when(mockPartModel.getParentPartModel()).thenReturn(mockParentModel);
        Mockito.when(mockParentModel.getPartNumber(mockExecutionContext, mockPartModel, PartType.FIELD)).thenReturn(expectedValue.longValue());

        assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testNumberOfCardButton() throws HtException {
        PartModel mockPartModel = Mockito.mock(ButtonModel.class);
        CardModel mockCardModel = Mockito.mock(CardModel.class);

        Mockito.when(partArgument.factor(Mockito.eq(mockExecutionContext), Mockito.eq(PartExp.class), Mockito.any(HtSemanticException.class))).thenReturn(mockPartExp);
        Mockito.when(mockPartExp.evaluateAsSpecifier(mockExecutionContext)).thenReturn(mockPartSpec);
        Mockito.when(mockExecutionContext.getPart(mockPartSpec)).thenReturn(mockPartModel);
        Mockito.when(mockPartModel.getParentPartModel()).thenReturn(mockCardModel);
        Mockito.when(mockCardModel.getPartNumber(mockExecutionContext, mockPartModel, PartType.BUTTON)).thenReturn(123L);

        assertEquals(new Value(123), uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testNumberOfBkgndButton() throws HtException {
        PartModel mockPartModel = Mockito.mock(ButtonModel.class);
        BackgroundModel mockCardModel = Mockito.mock(BackgroundModel.class);

        Mockito.when(partArgument.factor(Mockito.eq(mockExecutionContext), Mockito.eq(PartExp.class), Mockito.any(HtSemanticException.class))).thenReturn(mockPartExp);
        Mockito.when(mockPartExp.evaluateAsSpecifier(mockExecutionContext)).thenReturn(mockPartSpec);
        Mockito.when(mockExecutionContext.getPart(mockPartSpec)).thenReturn(mockPartModel);
        Mockito.when(mockPartModel.getParentPartModel()).thenReturn(mockCardModel);
        Mockito.when(mockCardModel.getPartNumber(mockExecutionContext, mockPartModel, PartType.BUTTON)).thenReturn(123L);

        assertEquals(new Value(123), uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testBogusPart() throws HtException {
        Mockito.when(partArgument.factor(Mockito.eq(mockExecutionContext), Mockito.eq(PartExp.class), Mockito.any(HtSemanticException.class))).thenThrow(new HtSemanticException("No such part!"));
        assertThrows(HtSemanticException.class, () -> uut.onEvaluate(mockExecutionContext));
    }

}
