package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValueTest {

    Value valueUnderTest;

    @Test
    public void testIsInteger() {
        assertTrue(new Value().isInteger());
        assertTrue(new Value("0").isInteger());
        assertTrue(new Value("123").isInteger());
        assertTrue(new Value("-123").isInteger());
        assertFalse(new Value("123.456").isInteger());
        assertFalse(new Value("-123.456").isInteger());
        assertFalse(new Value("true").isInteger());
        assertFalse(new Value("false").isInteger());
        assertFalse(new Value("1,2").isInteger());
        assertFalse(new Value("1,2,3,4").isInteger());
        assertFalse(new Value("abc123").isInteger());

    }

    @Test
    public void testIsNatural() {
        assertFalse(new Value().isNatural());
        assertFalse(new Value("0").isNatural());
        assertTrue(new Value("123").isNatural());
        assertFalse(new Value("-123").isNatural());
        assertFalse(new Value("123.456").isNatural());
        assertFalse(new Value("-123.456").isNatural());
        assertFalse(new Value("true").isNatural());
        assertFalse(new Value("false").isNatural());
        assertFalse(new Value("1,2").isNatural());
        assertFalse(new Value("1,2,3,4").isNatural());
        assertFalse(new Value("abc123").isInteger());

    }

    @Test
    public void testIsBoolean() {
        assertFalse(new Value().isBoolean());
        assertFalse(new Value("0").isBoolean());
        assertFalse(new Value("123").isBoolean());
        assertFalse(new Value("-123").isBoolean());
        assertFalse(new Value("123.456").isBoolean());
        assertFalse(new Value("-123.456").isBoolean());
        assertTrue(new Value("true").isBoolean());
        assertTrue(new Value("false").isBoolean());
        assertFalse(new Value("1,2").isBoolean());
        assertFalse(new Value("1,2,3,4").isBoolean());
        assertFalse(new Value("abc123").isInteger());
    }

    @Test
    public void testIsNumber() {
        assertTrue(new Value().isNumber());
        assertTrue(new Value("0").isNumber());
        assertTrue(new Value("123").isNumber());
        assertTrue(new Value("-123").isNumber());
        assertTrue(new Value("123.456").isNumber());
        assertTrue(new Value("-123.456").isNumber());
        assertFalse(new Value("true").isNumber());
        assertFalse(new Value("false").isNumber());
        assertFalse(new Value("1,2").isNumber());
        assertFalse(new Value("1,2,3,4").isNumber());
        assertFalse(new Value("abc123").isInteger());
    }

    @Test
    public void testIsPoint() {
        assertFalse(new Value().isPoint());
        assertFalse(new Value("0").isPoint());
        assertFalse(new Value("123").isPoint());
        assertFalse(new Value("-123").isPoint());
        assertFalse(new Value("123.456").isPoint());
        assertFalse(new Value("-123.456").isPoint());
        assertFalse(new Value("true").isPoint());
        assertFalse(new Value("false").isPoint());
        assertTrue(new Value("1,2").isPoint());
        assertFalse(new Value("1.2,2.3").isPoint());
        assertFalse(new Value("a,b").isPoint());
        assertFalse(new Value("1,2,3,4").isPoint());
        assertFalse(new Value("abc123").isInteger());
    }

    @Test
    public void testIsRect() {
        assertFalse(new Value().isRect());
        assertFalse(new Value("0").isRect());
        assertFalse(new Value("123").isRect());
        assertFalse(new Value("-123").isRect());
        assertFalse(new Value("123.456").isRect());
        assertFalse(new Value("-123.456").isRect());
        assertFalse(new Value("true").isRect());
        assertFalse(new Value("false").isRect());
        assertFalse(new Value("1,2").isRect());
        assertFalse(new Value("a,b,c,d").isRect());
        assertFalse(new Value("1.2,2.3,3.4,4.5").isRect());
        assertTrue(new Value("1,2,3,4").isRect());
        assertFalse(new Value("abc123").isInteger());
    }

    @Test
    public void testStringValue() {
        assertEquals("", new Value().toString());
        assertEquals("0", new Value("0").toString());
        assertEquals("123", new Value("123").toString());
        assertEquals("-123", new Value("-123").toString());
        assertEquals("123.456", new Value("123.456").toString());
        assertEquals("-123.456", new Value("-123.456").toString());
        assertEquals("true", new Value("true").toString());
        assertEquals("false", new Value("false").toString());
        assertEquals("1,2", new Value("1,2").toString());
        assertEquals("a,b,c,d", new Value("a,b,c,d").toString());
        assertEquals("1.2,2.3,3.4,4.5", new Value("1.2,2.3,3.4,4.5").toString());
        assertEquals("1,2,3,4", new Value("1,2,3,4").toString());
        assertEquals("abc123", new Value("abc123").toString());
    }

    @Test
    public void testIntegerValue() {
        assertEquals(0, new Value().integerValue());
        assertEquals(0, new Value("0").integerValue());
        assertEquals(123, new Value("123").integerValue());
        assertEquals(-123, new Value("-123").integerValue());
        assertEquals(0, new Value("123.456").integerValue());
        assertEquals(0, new Value("-123.456").integerValue());
        assertEquals(0, new Value("true").integerValue());
        assertEquals(0, new Value("false").integerValue());
        assertEquals(0, new Value("1,2").integerValue());
        assertEquals(0, new Value("a,b,c,d").integerValue());
        assertEquals(0, new Value("1.2,2.3,3.4,4.5").integerValue());
        assertEquals(0, new Value("1,2,3,4").integerValue());
        assertEquals(0, new Value("abc123").integerValue());
    }

    @Test
    public void testLongValue() {
        assertEquals(0L, new Value().longValue());
        assertEquals(0L, new Value("0").longValue());
        assertEquals(123L, new Value("123").longValue());
        assertEquals(-123L, new Value("-123").longValue());
        assertEquals(0L, new Value("123.456").longValue());
        assertEquals(0L, new Value("-123.456").longValue());
        assertEquals(0L, new Value("true").longValue());
        assertEquals(0L, new Value("false").longValue());
        assertEquals(0L, new Value("1,2").longValue());
        assertEquals(0L, new Value("a,b,c,d").longValue());
        assertEquals(0L, new Value("1.2,2.3,3.4,4.5").longValue());
        assertEquals(0L, new Value("1,2,3,4").longValue());
        assertEquals(0L, new Value("abc123").longValue());
    }

    @Test
    public void testDoubleValue() {
        assertEquals(0.0, new Value().doubleValue(), 0.0001);
        assertEquals(0.0, new Value("0").doubleValue(), 0.0001);
        assertEquals(123.0, new Value("123").doubleValue(), 0.0001);
        assertEquals(-123.0, new Value("-123").doubleValue(), 0.0001);
        assertEquals(123.456, new Value("123.456").doubleValue(), 0.0001);
        assertEquals(-123.456, new Value("-123.456").doubleValue(), 0.0001);
        assertEquals(0.0, new Value("true").doubleValue(), 0.0001);
        assertEquals(0.0, new Value("false").doubleValue(), 0.0001);
        assertEquals(0.0, new Value("1,2").doubleValue(), 0.0001);
        assertEquals(0.0, new Value("a,b,c,d").doubleValue(), 0.0001);
        assertEquals(0.0, new Value("1.2,2.3,3.4,4.5").doubleValue(), 0.0001);
        assertEquals(0.0, new Value("1,2,3,4").doubleValue(), 0.0001);
        assertEquals(0.0, new Value("abc123").doubleValue(), 0.0001);
    }

    @Test
    public void testDoubleValueOrError() throws Exception {
        assertEquals(0.0, new Value().doubleValueOrError(new HtException("")), 0.0001);
        assertEquals(0.0, new Value("0").doubleValueOrError(new HtException("")), 0.0001);
        assertEquals(123.0, new Value("123").doubleValueOrError(new HtException("")), 0.0001);
        assertEquals(-123.0, new Value("-123").doubleValueOrError(new HtException("")), 0.0001);
        assertEquals(123.456, new Value("123.456").doubleValueOrError(new HtException("")), 0.0001);
        assertEquals(-123.456, new Value("-123.456").doubleValueOrError(new HtException("")), 0.0001);
        assertThrows(HtException.class, () -> new Value("true").doubleValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("false").doubleValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("1,2").doubleValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("a,b,c,d").doubleValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("1.2,2.3,3.4,4.5").doubleValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("1,2,3,4").doubleValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("abc123").doubleValueOrError(new HtException("")));
    }

    @Test
    public void testBooleanValue() {
        assertFalse(new Value().booleanValue());
        assertFalse(new Value("0").booleanValue());
        assertFalse(new Value("123").booleanValue());
        assertFalse(new Value("-123").booleanValue());
        assertFalse(new Value("123.456").booleanValue());
        assertFalse(new Value("-123.456").booleanValue());
        assertTrue(new Value("true").booleanValue());
        assertFalse(new Value("false").booleanValue());
        assertFalse(new Value("1,2").booleanValue());
        assertFalse(new Value("a,b,c,d").booleanValue());
        assertFalse(new Value("1.2,2.3,3.4,4.5").booleanValue());
        assertFalse(new Value("1,2,3,4").booleanValue());
        assertFalse(new Value("abc123").booleanValue());
    }

    @Test
    public void testBooleanValueOrError() throws Exception {
        assertThrows(HtException.class, () -> new Value().booleanValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("0").booleanValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("123").booleanValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("-123").booleanValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("123.456").booleanValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("-123.456").booleanValueOrError(new HtException("")));
        assertTrue(new Value("true").booleanValueOrError(new HtException("")));
        assertFalse(new Value("false").booleanValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("1,2").booleanValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("a,b,c,d").booleanValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("1.2,2.3,3.4,4.5").booleanValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("1,2,3,4").booleanValueOrError(new HtException("")));
        assertThrows(HtException.class, () -> new Value("abc123").booleanValueOrError(new HtException("")));
    }

    @Test
    public void testRectangleValue() {
        // Setup
        final ExecutionContext context = null;
        final Rectangle expectedResult = null;

        // Run the test
        final Rectangle result = valueUnderTest.rectangleValue(context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testPointValue() {
        // Setup
        final ExecutionContext context = null;
        final Point expectedResult = null;

        // Run the test
        final Point result = valueUnderTest.pointValue(context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetListItems() {
        // Setup
        final List<Value> expectedResult = Arrays.asList();

        // Run the test
        final List<Value> result = valueUnderTest.getListItems();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetItems() {
        // Setup
        final ExecutionContext context = null;
        final List<Value> expectedResult = Arrays.asList();

        // Run the test
        final List<Value> result = valueUnderTest.getItems(context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetItemAt() {
        // Setup
        final ExecutionContext context = null;
        final int index = 0;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.getItemAt(context, index);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetLines() {
        // Setup
        final ExecutionContext context = null;
        final List<Value> expectedResult = Arrays.asList();

        // Run the test
        final List<Value> result = valueUnderTest.getLines(context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetWords() {
        // Setup
        final ExecutionContext context = null;
        final List<Value> expectedResult = Arrays.asList();

        // Run the test
        final List<Value> result = valueUnderTest.getWords(context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetChars() {
        // Setup
        final ExecutionContext context = null;
        final List<Value> expectedResult = Arrays.asList();

        // Run the test
        final List<Value> result = valueUnderTest.getChars(context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetChunks() {
        // Setup
        final ExecutionContext context = null;
        final ChunkType type = null;
        final List<Value> expectedResult = Arrays.asList();

        // Run the test
        final List<Value> result = valueUnderTest.getChunks(context, type);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testItemCount() {
        // Setup
        final ExecutionContext context = null;
        final int expectedResult = 0;

        // Run the test
        final int result = valueUnderTest.itemCount(context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testWordCount() {
        // Setup
        final ExecutionContext context = null;
        final int expectedResult = 0;

        // Run the test
        final int result = valueUnderTest.wordCount(context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testCharCount() {
        // Setup
        final ExecutionContext context = null;
        final int expectedResult = 0;

        // Run the test
        final int result = valueUnderTest.charCount(context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testLineCount() {
        // Setup
        final ExecutionContext context = null;
        final int expectedResult = 0;

        // Run the test
        final int result = valueUnderTest.lineCount(context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetChunk() throws Exception {
        // Setup
        final ExecutionContext context = null;
        final Chunk c = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.getChunk(context, c);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testIsEmpty() {
        // Setup
        final boolean expectedResult = false;

        // Run the test
        final boolean result = valueUnderTest.isEmpty();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testLessThan() {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.isLessThan(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGreaterThan() {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.isGreaterThan(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGreaterThanOrEqualTo() {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.isGreaterThanOrEqualTo(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testLessThanOrEqualTo() {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.isLessThanOrEqualTo(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testMultiply() throws Exception {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.multiply(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testDivide() throws Exception {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.divide(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testAdd() throws Exception {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.add(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testSubtract() throws Exception {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.subtract(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testExponentiate() throws Exception {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.exponentiate(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testMod() throws Exception {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.mod(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testNot() throws Exception {
        // Setup
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.not();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testNegate() throws Exception {
        // Setup
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.negate();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testAnd() throws Exception {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.and(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testOr() throws Exception {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.or(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testConcat() {
        // Setup
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.concat(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testWithin() throws Exception {
        // Setup
        final ExecutionContext context = null;
        final Value v = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.within(context, v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testTrunc() throws Exception {
        // Setup
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.trunc();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testIsA() throws Exception {
        // Setup
        final Value val = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = valueUnderTest.isA(val);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testContains() {
        // Setup
        final Value v = null;
        final boolean expectedResult = false;

        // Run the test
        final boolean result = valueUnderTest.contains(v);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testToString() {
        // Setup
        final String expectedResult = "result";

        // Run the test
        final String result = valueUnderTest.toString();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testEquals() {
        // Setup
        final Object o = null;
        final boolean expectedResult = false;

        // Run the test
        final boolean result = valueUnderTest.equals(o);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testHashCode() {
        // Setup
        final int expectedResult = 0;

        // Run the test
        final int result = valueUnderTest.hashCode();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testCompareTo() {
        // Setup
        final Value o = null;
        final int expectedResult = 0;

        // Run the test
        final int result = valueUnderTest.compareTo(o);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testCompareTo1() {
        // Setup
        final Value to = null;
        final SortStyle style = null;
        final int expectedResult = 0;

        // Run the test
        final int result = valueUnderTest.compareTo(to, style);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testOfQuotedLiteral() {
        // Setup
        final String literal = "literal";
        final Value expectedResult = null;

        // Run the test
        final Value result = Value.ofQuotedLiteral(literal);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testOfLines() {
        // Setup
        final List<Value> lines = Arrays.asList();
        final Value expectedResult = null;

        // Run the test
        final Value result = Value.ofLines(lines);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testOfItems() {
        // Setup
        final List<Value> items = Arrays.asList();
        final Value expectedResult = null;

        // Run the test
        final Value result = Value.ofItems(items);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testOfWords() {
        // Setup
        final List<Value> words = Arrays.asList();
        final Value expectedResult = null;

        // Run the test
        final Value result = Value.ofWords(words);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testOfChars() {
        // Setup
        final List<Value> chars = Arrays.asList();
        final Value expectedResult = null;

        // Run the test
        final Value result = Value.ofChars(chars);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testSetChunk() throws Exception {
        // Setup
        final ExecutionContext context = null;
        final Value mutable = null;
        final Preposition p = null;
        final Chunk c = null;
        final Object mutator = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = Value.ofChunk(context, mutable, p, c, mutator);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testSetValue() {
        // Setup
        final Value mutable = null;
        final Preposition p = null;
        final Value mutator = null;
        final Value expectedResult = null;

        // Run the test
        final Value result = Value.ofValue(mutable, p, mutator);

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
