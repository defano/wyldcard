package com.defano.wyldcard.properties;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertiesModelTest extends GuiceTest {

    @Mock private ExecutionContext context;
    private SimplePropertiesModel model = new SimplePropertiesModel();

    @Test
    public void testBasicPropertyExists() {
        model.define("basic").asValue(10);
        assertEquals(new Value(10), model.get(context, "basic"));
    }

    @Test
    public void testBasicPropertyCanBeSet() {
        model.define("basic").asValue(20);
        assertEquals(new Value(20), model.get(context, "basic"));

        model.set(context, "basic", new Value(30));
        assertEquals(new Value(30), model.get(context, "basic"));
    }

    @Test void testThatPropertyAliasesWork() {
        model.define("one", "two", "three").asConstant(15);
        assertEquals(new Value(15), model.get(context, "one"));
        assertEquals(new Value(15), model.get(context, "two"));
        assertEquals(new Value(15), model.get(context, "three"));
    }

    @Test
    public void testThatPropertyNamesAreCaseInsensitive() {
        model.define("basic").asValue(10);
        assertEquals(new Value(10), model.get(context, "BASIC"));
    }

    @Test
    public void testThatAliasedPropertyNamesAreCaseInsensitive() {
        model.define("one", "two", "three").asConstant(15);
        assertEquals(new Value(15), model.get(context, "ONE"));
        assertEquals(new Value(15), model.get(context, "TWO"));
        assertEquals(new Value(15), model.get(context, "THREE"));
    }

    @Test
    public void testThatDelegatedPropertiesDelegateToModel() {
        SimplePropertiesModel delegate = new SimplePropertiesModel();
        delegate.define("this").asConstant(10);
        delegate.define("that").asConstant(20);

        model.define("this", "that").byDelegatingToModel(context -> delegate);
        assertEquals(new Value(10), model.get(context, "this"));
        assertEquals(new Value(20), model.get(context, "that"));
    }

}
