package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.part.builder.FieldModelBuilder;
import com.defano.wyldcard.part.field.FieldModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ClickLineFuncTest extends GuiceTest<ClickLineFunc> {

    @BeforeEach
    public void setup() {
        initialize(new ClickLineFunc(mockParserRuleContext));
    }

    @Test
    public void testThatClickLineReturnsClickLine() throws HtException {
        Value expectedLineClickInfo = new Value("line 1 of card field 2");
        FieldModelBuilder builder = new FieldModelBuilder(Owner.CARD, mockWyldCardPart);
        FieldModel fieldModel = builder.withText("first\nsecond\nthird").withPartNumber(2).build();
        fieldModel.setOwner(Owner.CARD);
        PartSpecifier target = fieldModel.getPartSpecifier(mockExecutionContext);
        when(mockSelectionManager.getClickLine()).thenReturn(new Value(1));
        when(mockExecutionContext.getTarget()).thenReturn(fieldModel.getPartSpecifier(mockExecutionContext));
        when(mockExecutionContext.getProperty("number", target)).thenReturn(new Value(2));
        assertEquals(uut.onEvaluate(mockExecutionContext), expectedLineClickInfo);
    }

}