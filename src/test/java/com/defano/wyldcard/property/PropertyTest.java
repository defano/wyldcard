package com.defano.wyldcard.property;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.property.value.BasicValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertyTest {

    @Test
    public void testThatSingleNameIsEqual() {
        Property p1 = new Property(new BasicValue(new Value("x")), "prop");
        Property p2 = new Property(new BasicValue(new Value("x")), "prop");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testThatAliasesEqual() {
        Property p1 = new Property(new BasicValue(new Value("x")), "prop", "property", "propertee");
        Property p2 = new Property(new BasicValue(new Value("x")), "prop", "property", "propertee");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void testThatOutOfOrderAliasesEqual() {
        Property p1 = new Property(new BasicValue(new Value("x")), "property", "prop", "propertee");
        Property p2 = new Property(new BasicValue(new Value("x")), "prop", "property", "propertee");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

}
