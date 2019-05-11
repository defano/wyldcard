package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expressions.CountableExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.enums.Countable;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.parts.wyldcard.WyldCardProperties;
import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class NumberFuncTest extends GuiceTest<NumberFunc> {

    private Expression mockCountableExp = Mockito.mock(Expression.class);

    @BeforeEach
    public void setUp() {
        initialize();
    }

    @Test
    public void testNumberOfChars() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.CHARS_OF, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            Mockito.when(mockCountableExp.evaluate(mockExecutionContext)).thenReturn(valueOfChars(testValue));
            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfWords() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.WORDS_OF, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            Mockito.when(mockCountableExp.evaluate(mockExecutionContext)).thenReturn(new Value(valueOfWords(testValue)));
            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfItems() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.ITEMS_OF, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            Mockito.when(mockWyldCardPart.get(mockExecutionContext, WyldCardProperties.PROP_ITEMDELIMITER)).thenReturn(new Value(","));
            Mockito.when(mockCountableExp.evaluate(mockExecutionContext)).thenReturn(new Value(valueOfItems(testValue)));

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfLines() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.LINES_OF, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            Mockito.when(mockCountableExp.evaluate(mockExecutionContext)).thenReturn(new Value(valueOfLines(testValue)));
            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfMenuItems() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.MENU_ITEMS, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            JMenu mockMenu = Mockito.mock(JMenu.class);

            Mockito.when(mockCountableExp.evaluate(mockExecutionContext)).thenReturn(new Value("My Test Menu"));
            Mockito.when(mockWyldCardMenuBar.findMenuByName("My Test Menu")).thenReturn(mockMenu);
            Mockito.when(mockMenu.getItemCount()).thenReturn(testValue);

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfMenus() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.MENUS, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            Mockito.when(mockWyldCardMenuBar.getVisibleMenus().size()).thenReturn(testValue);
            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfCardsInThisStack() throws Exception {
        for (int testValue : nonNegativeIntValues()) {

            Observable<Integer> cardCountProvider = BehaviorSubject.createDefault(testValue);
            NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.CARDS, null));
            Value expectedValue = new Value(testValue);

            Mockito.when(mockExecutionContext.getCurrentStack().getCardCountProvider()).thenReturn(cardCountProvider);

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfCardsInBkgnd() throws Exception {
        BackgroundModel mockBackground = Mockito.mock(BackgroundModel.class);
        StackModel mockStack = Mockito.mock(StackModel.class);
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.CARDS, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            Mockito.when(mockCountableExp.partFactor(mockExecutionContext, BackgroundModel.class)).thenReturn(mockBackground);
            Mockito.when(mockCountableExp.partFactor(eq(mockExecutionContext), eq(StackModel.class), Mockito.any(HtSemanticException.class))).thenReturn(mockStack);
            Mockito.when(mockBackground.getCardModels()).thenReturn(listOfMocks(CardModel.class, testValue));

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfMarkedCardsInBkgnd() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.MARKED_CARDS, mockCountableExp));
        Value expectedValue = new Value(2);

        CardModel card1 = Mockito.mock(CardModel.class);
        CardModel card2 = Mockito.mock(CardModel.class);
        CardModel card3 = Mockito.mock(CardModel.class);

        BackgroundModel mockBackground = Mockito.mock(BackgroundModel.class);
        StackModel mockStack = Mockito.mock(StackModel.class);

        Mockito.when(card1.isMarked(mockExecutionContext)).thenReturn(true);
        Mockito.when(card2.isMarked(mockExecutionContext)).thenReturn(false);
        Mockito.when(card3.isMarked(mockExecutionContext)).thenReturn(true);

        Mockito.when(mockCountableExp.partFactor(mockExecutionContext, BackgroundModel.class)).thenReturn(mockBackground);
        Mockito.when(mockCountableExp.partFactor(eq(mockExecutionContext), eq(StackModel.class), Mockito.any(HtSemanticException.class))).thenReturn(mockStack);
        Mockito.when(mockBackground.getCardModels()).thenReturn(Lists.newArrayList(
                card1, card2, card3
        ));

        assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testNumberOfCardsInSpecifiedStack() throws Exception {
        StackModel mockStack = Mockito.mock(StackModel.class);

        for (int testValue : nonNegativeIntValues()) {
            NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.CARDS, mockCountableExp));
            Value expectedValue = new Value(testValue);

            Mockito.when(mockCountableExp.partFactor(mockExecutionContext, BackgroundModel.class)).thenReturn(null);
            Mockito.when(mockCountableExp.partFactor(eq(mockExecutionContext), eq(StackModel.class), any(HtSemanticException.class))).thenReturn(mockStack);
            Mockito.when(mockStack.getCardCount()).thenReturn(testValue);

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfMarkedCardsInSpecifiedStack() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.MARKED_CARDS, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            StackModel mockStack = Mockito.mock(StackModel.class);

            Mockito.when(mockCountableExp.partFactor(mockExecutionContext, BackgroundModel.class)).thenReturn(null);
            Mockito.when(mockCountableExp.partFactor(eq(mockExecutionContext), eq(StackModel.class), any(HtSemanticException.class))).thenReturn(mockStack);
            Mockito.when(mockStack.getMarkedCards(mockExecutionContext)).thenReturn(listOfMocks(CardModel.class, testValue));

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfBkgndsInThisStack() throws HtException {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.BACKGROUNDS, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            BackgroundModel mockBackground = Mockito.mock(BackgroundModel.class);

            Mockito.when(mockCountableExp.partFactor(mockExecutionContext, BackgroundModel.class)).thenReturn(mockBackground);
            Mockito.when(mockBackground.getCardModels()).thenReturn(listOfMocks(CardModel.class, testValue));

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfBkgndsInSpecifiedStack() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.BACKGROUNDS, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {

            Value expectedValue = new Value(testValue);

            StackModel mockStack = Mockito.mock(StackModel.class);

            Mockito.when(mockCountableExp.partFactor(mockExecutionContext, BackgroundModel.class)).thenReturn(null);
            Mockito.when(mockCountableExp.partFactor(eq(mockExecutionContext), eq(StackModel.class), any(HtSemanticException.class))).thenReturn(mockStack);
            Mockito.when(mockStack.getBackgroundCount()).thenReturn(expectedValue.integerValue());

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfWindows() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.WINDOWS, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);
            Mockito.when(mockWindowManager.getFrames(false).size()).thenReturn(expectedValue.integerValue());
            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfCardPartsOnThisCard() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.CARD_PARTS, null));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            CardModel mockCardModel = Mockito.mock(CardModel.class);
            Mockito.when(mockExecutionContext.getCurrentCard().getPartModel()).thenReturn(mockCardModel);
            Mockito.when(mockCardModel.getPartCount(mockExecutionContext, null, Owner.CARD)).thenReturn(expectedValue.longValue());

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfCardPartsOnSpecifiedCard() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.CARD_PARTS, mockCountableExp));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            CardModel mockCardModel = Mockito.mock(CardModel.class);

            Mockito.when(mockCountableExp.partFactor(mockExecutionContext, BackgroundModel.class)).thenReturn(null);
            Mockito.when(mockCountableExp.partFactor(eq(mockExecutionContext), eq(CardModel.class), any(HtSemanticException.class))).thenReturn(mockCardModel);

            Mockito.when(mockExecutionContext.getCurrentCard().getPartModel()).thenReturn(mockCardModel);
            Mockito.when(mockCardModel.getPartCount(mockExecutionContext, null, Owner.CARD)).thenReturn(expectedValue.longValue());

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfBkgndPartsOnThisBkgnd() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.BKGND_PARTS, null));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            CardModel mockCardModel = Mockito.mock(CardModel.class);
            BackgroundModel mockBkgndModel = Mockito.mock(BackgroundModel.class);

            Mockito.when(mockExecutionContext.getCurrentCard().getPartModel()).thenReturn(mockCardModel);
            Mockito.when(mockCardModel.getBackgroundModel()).thenReturn(mockBkgndModel);
            Mockito.when(mockBkgndModel.getPartCount(mockExecutionContext, null, Owner.BACKGROUND)).thenReturn(expectedValue.longValue());

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfCardButtonsOnThisCard() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.CARD_BUTTONS, null));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            CardModel mockCardModel = Mockito.mock(CardModel.class);
            Mockito.when(mockExecutionContext.getCurrentCard().getPartModel()).thenReturn(mockCardModel);
            Mockito.when(mockCardModel.getPartCount(mockExecutionContext, PartType.BUTTON, Owner.CARD)).thenReturn(expectedValue.longValue());

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfCardFieldsOnThisCard() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.CARD_FIELDS, null));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            CardModel mockCardModel = Mockito.mock(CardModel.class);
            Mockito.when(mockExecutionContext.getCurrentCard().getPartModel()).thenReturn(mockCardModel);
            Mockito.when(mockCardModel.getPartCount(mockExecutionContext, PartType.FIELD, Owner.CARD)).thenReturn(expectedValue.longValue());

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfBkgndButtonsOnThisCard() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.BKGND_BUTTONS, null));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            CardModel mockCardModel = Mockito.mock(CardModel.class);
            BackgroundModel mockBkgndModel = Mockito.mock(BackgroundModel.class);

            Mockito.when(mockExecutionContext.getCurrentCard().getPartModel()).thenReturn(mockCardModel);
            Mockito.when(mockCardModel.getBackgroundModel()).thenReturn(mockBkgndModel);
            Mockito.when(mockBkgndModel.getPartCount(mockExecutionContext, PartType.BUTTON, Owner.BACKGROUND)).thenReturn(expectedValue.longValue());

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

    @Test
    public void testNumberOfBkgndFieldsOnThisCard() throws Exception {
        NumberFunc uut = new NumberFunc(mockParserRuleContext, new CountableExp(mockParserRuleContext, Countable.BKGND_FIELDS, null));

        for (int testValue : nonNegativeIntValues()) {
            Value expectedValue = new Value(testValue);

            CardModel mockCardModel = Mockito.mock(CardModel.class);
            BackgroundModel mockBkgndModel = Mockito.mock(BackgroundModel.class);

            Mockito.when(mockExecutionContext.getCurrentCard().getPartModel()).thenReturn(mockCardModel);
            Mockito.when(mockCardModel.getBackgroundModel()).thenReturn(mockBkgndModel);
            Mockito.when(mockBkgndModel.getPartCount(mockExecutionContext, PartType.FIELD, Owner.BACKGROUND)).thenReturn(expectedValue.longValue());

            assertEquals(expectedValue, uut.onEvaluate(mockExecutionContext));
        }
    }

}
