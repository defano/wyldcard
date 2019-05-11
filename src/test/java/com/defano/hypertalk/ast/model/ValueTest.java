package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.chunk.ChunkType;
import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.ast.model.enums.SortStyle;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.TestChunkBuilder;
import com.defano.wyldcard.parts.wyldcard.WyldCardProperties;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class ValueTest extends GuiceTest<Value> {

    @BeforeEach
    public void setUp() {
        initialize(new Value(mockParserRuleContext));
    }

    @Test
    public void testIsInteger() {
        assertFalse(new Value().isInteger());
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
        assertEquals(new Rectangle(1, 2, 2, 2), new Value("1,2,3,4").rectangleValue());
        assertNotEquals(new Rectangle(1, 2, 2, 2), new Value("10,10,10,10").rectangleValue());
    }

    @Test
    public void testPointValue() {
        assertEquals(new Point(1,2), new Value("1, 2").pointValue());
    }

    @Test
    public void testGetListItems() {
        assertIterableEquals(Lists.newArrayList(), new Value().getListItems());
        assertIterableEquals(Lists.newArrayList(new Value("1")), new Value("1").getListItems());
        assertIterableEquals(Lists.newArrayList(new Value("1"), new Value("2")), new Value("1,2").getListItems());
    }

    @Test
    public void testGetItems() {
        Mockito.when(mockWyldCardPart.get(mockExecutionContext, WyldCardProperties.PROP_ITEMDELIMITER)).thenReturn(new Value(","));
        assertIterableEquals(Lists.newArrayList(), new Value().getItems(mockExecutionContext));
        assertIterableEquals(Lists.newArrayList(new Value("1")), new Value("1").getItems(mockExecutionContext));
        assertIterableEquals(Lists.newArrayList(new Value("1"), new Value("2")), new Value("1,2").getItems(mockExecutionContext));

        Mockito.when(mockWyldCardPart.get(mockExecutionContext, WyldCardProperties.PROP_ITEMDELIMITER)).thenReturn(new Value("-"));
        assertIterableEquals(Lists.newArrayList(), new Value().getItems(mockExecutionContext));
        assertIterableEquals(Lists.newArrayList(new Value("1")), new Value("1").getItems(mockExecutionContext));
        assertIterableEquals(Lists.newArrayList(new Value("1"), new Value("2")), new Value("1-2").getItems(mockExecutionContext));
    }

    @Test
    public void testGetItemAt() {
        Mockito.when(mockWyldCardPart.get(mockExecutionContext, WyldCardProperties.PROP_ITEMDELIMITER)).thenReturn(new Value(","));
        assertEquals(new Value(), new Value().getItemAt(mockExecutionContext, 0));
        assertEquals(new Value("1"), new Value("1, 2, 3").getItemAt(mockExecutionContext, 0));
        assertEquals(new Value("2"), new Value("1, 2, 3").getItemAt(mockExecutionContext, 1));
        assertEquals(new Value("3"), new Value("1, 2, 3").getItemAt(mockExecutionContext, 2));
        assertEquals(new Value(), new Value("1, 2, 3").getItemAt(mockExecutionContext, 3));
    }

    @Test
    public void testGetLines() {
        assertIterableEquals(Lists.newArrayList(
                new Value(1), new Value(2), new Value(3)),
                new Value("1\n2\n3").getLines(mockExecutionContext));

        assertIterableEquals(Lists.newArrayList(), new Value().getLines(mockExecutionContext));
    }

    @Test
    public void testGetWords() {
        assertIterableEquals(Lists.newArrayList(
                new Value("one"), new Value("two"), new Value("three"), new Value("four")),
                new Value("one two   three\t\t\tfour").getWords(mockExecutionContext));

        assertIterableEquals(Lists.newArrayList(),
                new Value().getWords(mockExecutionContext));
    }

    @Test
    public void testGetChars() {
        assertIterableEquals(Lists.newArrayList(
                new Value("A"), new Value("b"), new Value("c")),
                new Value("Abc").getChars(mockExecutionContext));

        assertIterableEquals(Lists.newArrayList(),
                new Value().getChars(mockExecutionContext));
    }

    @Test
    public void testGetChunksGetsChars() {

        // Chars

        for (ChunkType charType : Lists.newArrayList(ChunkType.CHAR, ChunkType.CHARRANGE)) {
            assertIterableEquals(Lists.newArrayList(),
                    new Value().getChunks(mockExecutionContext, charType));

            assertIterableEquals(Lists.newArrayList(
                    new Value("a")),
                    new Value("a").getChunks(mockExecutionContext, charType));

            assertIterableEquals(Lists.newArrayList(
                    new Value("a"), new Value(" "), new Value("c")),
                    new Value("a c").getChunks(mockExecutionContext, charType));
        }
    }

    @Test
    public void testGetChunksGetsItems() {
        Mockito.when(mockWyldCardPart.get(mockExecutionContext, WyldCardProperties.PROP_ITEMDELIMITER)).thenReturn(new Value(","));

        for (ChunkType itemType : Lists.newArrayList(ChunkType.ITEM, ChunkType.ITEMRANGE)) {
            assertIterableEquals(Lists.newArrayList(),
                    new Value().getChunks(mockExecutionContext, itemType));

            assertIterableEquals(Lists.newArrayList(
                    new Value("a")),
                    new Value("a").getChunks(mockExecutionContext, itemType));

            assertIterableEquals(Lists.newArrayList(
                    new Value("a"), new Value("c")),
                    new Value("a,c").getChunks(mockExecutionContext, itemType));
        }
    }

    @Test
    public void testGetChunksGetsWords() {
        for (ChunkType wordType : Lists.newArrayList(ChunkType.WORD, ChunkType.WORDRANGE)) {
            assertIterableEquals(Lists.newArrayList(),
                    new Value().getChunks(mockExecutionContext, wordType));

            assertIterableEquals(Lists.newArrayList(
                    new Value("one")),
                    new Value("one").getChunks(mockExecutionContext, wordType));

            assertIterableEquals(Lists.newArrayList(
                    new Value("one"), new Value("two"), new Value("three")),
                    new Value("one two three").getChunks(mockExecutionContext, wordType));
        }
    }

    @Test
    public void testGetChunksGetsLines() {
        for (ChunkType lineType : Lists.newArrayList(ChunkType.LINE, ChunkType.LINERANGE)) {
            assertIterableEquals(Lists.newArrayList(),
                    new Value().getChunks(mockExecutionContext, lineType));

            assertIterableEquals(Lists.newArrayList(
                    new Value("one")),
                    new Value("one").getChunks(mockExecutionContext, lineType));

            assertIterableEquals(Lists.newArrayList(
                    new Value("one"), new Value("two"), new Value("three")),
                    new Value("one\ntwo\nthree").getChunks(mockExecutionContext, lineType));
        }
    }

    @Test
    public void testItemCount() {
        Mockito.when(mockWyldCardPart.get(mockExecutionContext, WyldCardProperties.PROP_ITEMDELIMITER)).thenReturn(new Value(","));

        assertEquals(0, new Value().itemCount(mockExecutionContext));
        assertEquals(1, new Value("one").itemCount(mockExecutionContext));
        assertEquals(2, new Value("one,two").itemCount(mockExecutionContext));
        assertEquals(4, new Value(",,,").itemCount(mockExecutionContext));
        assertEquals(5, new Value("one,,,four,").itemCount(mockExecutionContext));
    }

    @Test
    public void testWordCount() {
        assertEquals(0, new Value().wordCount(mockExecutionContext));
        assertEquals(1, new Value("one").wordCount(mockExecutionContext));
        assertEquals(2, new Value("one two").wordCount(mockExecutionContext));
        assertEquals(3, new Value("one two      three").wordCount(mockExecutionContext));
    }

    @Test
    public void testCharCount() {
        assertEquals(0, new Value().charCount(mockExecutionContext));
        assertEquals(1, new Value("1").charCount(mockExecutionContext));
        assertEquals(2, new Value("12").charCount(mockExecutionContext));
    }

    @Test
    public void testLineCount() {
        assertEquals(0, new Value().lineCount(mockExecutionContext));
        assertEquals(1, new Value("1").lineCount(mockExecutionContext));
        assertEquals(2, new Value("1\n2").lineCount(mockExecutionContext));
        assertEquals(3, new Value("1\n2\n3").lineCount(mockExecutionContext));
        assertEquals(6, new Value("1\n2\n3\n\n\n6").lineCount(mockExecutionContext));
    }

    @Test
    public void testGetChunkGetsChars() throws Exception {
        assertThrows(HtSemanticException.class, () -> new Value().getChunk(mockExecutionContext, TestChunkBuilder.buildSingleChunk(ChunkType.CHAR, -1)));
        assertThrows(HtSemanticException.class, () -> new Value().getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.CHARRANGE, -1, -1)));
        assertThrows(HtSemanticException.class, () -> new Value("abc123").getChunk(mockExecutionContext, TestChunkBuilder.buildSingleChunk(ChunkType.CHAR, 0)));
        assertEquals(new Value("a"), new Value("abc123").getChunk(mockExecutionContext, TestChunkBuilder.buildSingleChunk(ChunkType.CHAR, 1)));
        assertEquals(new Value("abc"), new Value("abc123").getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.CHARRANGE, 1, 3)));
        assertEquals(new Value("abc123"), new Value("abc123").getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.CHARRANGE, 1, 6)));
        assertEquals(new Value("abc123"), new Value("abc123").getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.CHARRANGE, 1, 300)));
    }

    @Test
    public void testGetChunkGetsWords() throws Exception {
        assertThrows(HtSemanticException.class, () -> new Value().getChunk(mockExecutionContext, TestChunkBuilder.buildSingleChunk(ChunkType.WORD, -1)));
        assertThrows(HtSemanticException.class, () -> new Value().getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.WORDRANGE, -1, -1)));
        assertThrows(HtSemanticException.class, () -> new Value("abc123").getChunk(mockExecutionContext, TestChunkBuilder.buildSingleChunk(ChunkType.WORD, 0)));
        assertEquals(new Value("two"), new Value("one two three").getChunk(mockExecutionContext, TestChunkBuilder.buildSingleChunk(ChunkType.WORD, 2)));
        assertEquals(new Value("one two"), new Value("one two three").getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.WORDRANGE, 1, 2)));
        assertEquals(new Value("one two three"), new Value("one two three").getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.WORDRANGE, 1, 3)));
        assertEquals(new Value("one two three"), new Value("one two three").getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.CHARRANGE, 1, 300)));
    }

    @Test
    public void testGetChunkGetsItems() throws Exception {
        Mockito.when(mockWyldCardPart.get(mockExecutionContext, WyldCardProperties.PROP_ITEMDELIMITER)).thenReturn(new Value(","));

        assertThrows(HtSemanticException.class, () -> new Value().getChunk(mockExecutionContext, TestChunkBuilder.buildSingleChunk(ChunkType.ITEM, -1)));
        assertThrows(HtSemanticException.class, () -> new Value().getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.ITEMRANGE, -1, -1)));
        assertThrows(HtSemanticException.class, () -> new Value("abc123").getChunk(mockExecutionContext, TestChunkBuilder.buildSingleChunk(ChunkType.ITEM, 0)));
        assertEquals(new Value(" two"), new Value("one, two, three").getChunk(mockExecutionContext, TestChunkBuilder.buildSingleChunk(ChunkType.ITEM, 2)));
        assertEquals(new Value("one, two"), new Value("one, two, three").getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.ITEMRANGE, 1, 2)));
        assertEquals(new Value("one, two, three"), new Value("one, two, three").getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.ITEMRANGE, 1, 3)));
        assertEquals(new Value("one, two, three"), new Value("one, two, three").getChunk(mockExecutionContext, TestChunkBuilder.buildChunkRange(ChunkType.ITEMRANGE, 1, 300)));
    }


    @Test
    public void testIsEmpty() {
        assertTrue(new Value().isEmpty());
        assertTrue(new Value("").isEmpty());
        assertTrue(Value.ofQuotedLiteral("").isEmpty());
        assertFalse(new Value(" ").isEmpty());
        assertFalse(new Value("abc123").isEmpty());
        assertFalse(new Value(1.2).isEmpty());
    }

    @Test
    public void testLessThan() {
        assertFalse(new Value(1).isLessThan(new Value(1)).booleanValue());
        assertTrue(new Value(1).isLessThan(new Value(2)).booleanValue());
        assertFalse(new Value(1.0).isLessThan(new Value(1.0)).booleanValue());
        assertTrue(new Value(1.0).isLessThan(new Value(2.0)).booleanValue());
        assertFalse(new Value(.01).isLessThan(new Value(.01)).booleanValue());
        assertTrue(new Value(.01).isLessThan(new Value(.02)).booleanValue());
        assertFalse(new Value(-1).isLessThan(new Value(-1)).booleanValue());
        assertTrue(new Value(-2).isLessThan(new Value(-1)).booleanValue());
        assertTrue(new Value(-2.0).isLessThan(new Value(-1.0)).booleanValue());
        assertFalse(new Value(-1.0).isLessThan(new Value(-1.0)).booleanValue());
        assertTrue(new Value(-.02).isLessThan(new Value(-.01)).booleanValue());
        assertFalse(new Value(-.01).isLessThan(new Value(-.01)).booleanValue());

        assertTrue(new Value("a").isLessThan(new Value("b")).booleanValue());
        assertFalse(new Value("a").isLessThan(new Value("a")).booleanValue());
        assertTrue(new Value("apple").isLessThan(new Value("banana")).booleanValue());
        assertFalse(new Value("apple").isLessThan(new Value("apple")).booleanValue());
        assertTrue(new Value(10).isLessThan(new Value("apple")).booleanValue());            // Lexical compare
        assertTrue(new Value(false).isLessThan(new Value(true)).booleanValue());            // Lexical compare
        assertFalse(new Value(false).isLessThan(new Value(false)).booleanValue());
    }

    @Test
    public void testGreaterThan() {
        assertFalse(new Value(1).isGreaterThan(new Value(1)).booleanValue());
        assertFalse(new Value(1).isGreaterThan(new Value(2)).booleanValue());
        assertFalse(new Value(1.0).isGreaterThan(new Value(1.0)).booleanValue());
        assertFalse(new Value(1.0).isGreaterThan(new Value(2.0)).booleanValue());
        assertFalse(new Value(.01).isGreaterThan(new Value(.01)).booleanValue());
        assertFalse(new Value(.01).isGreaterThan(new Value(.02)).booleanValue());
        assertFalse(new Value(-1).isGreaterThan(new Value(-1)).booleanValue());
        assertFalse(new Value(-2).isGreaterThan(new Value(-1)).booleanValue());
        assertFalse(new Value(-2.0).isGreaterThan(new Value(-1.0)).booleanValue());
        assertFalse(new Value(-1.0).isGreaterThan(new Value(-1.0)).booleanValue());
        assertFalse(new Value(-.02).isGreaterThan(new Value(-.01)).booleanValue());
        assertFalse(new Value(-.01).isGreaterThan(new Value(-.01)).booleanValue());

        assertFalse(new Value("a").isGreaterThan(new Value("b")).booleanValue());
        assertFalse(new Value("a").isGreaterThan(new Value("a")).booleanValue());
        assertFalse(new Value("apple").isGreaterThan(new Value("banana")).booleanValue());
        assertFalse(new Value("apple").isGreaterThan(new Value("apple")).booleanValue());
        assertFalse(new Value(10).isGreaterThan(new Value("apple")).booleanValue());            // Lexical compare
        assertFalse(new Value(false).isGreaterThan(new Value(true)).booleanValue());            // Lexical compare
        assertFalse(new Value(false).isGreaterThan(new Value(false)).booleanValue());
    }

    @Test
    public void testGreaterThanOrEqualTo() {
        assertTrue(new Value(1).isGreaterThanOrEqualTo(new Value(1)).booleanValue());
        assertFalse(new Value(1).isGreaterThanOrEqualTo(new Value(2)).booleanValue());
        assertTrue(new Value(1.0).isGreaterThanOrEqualTo(new Value(1.0)).booleanValue());
        assertFalse(new Value(1.0).isGreaterThanOrEqualTo(new Value(2.0)).booleanValue());
        assertTrue(new Value(.01).isGreaterThanOrEqualTo(new Value(.01)).booleanValue());
        assertFalse(new Value(.01).isGreaterThanOrEqualTo(new Value(.02)).booleanValue());
        assertTrue(new Value(-1).isGreaterThanOrEqualTo(new Value(-1)).booleanValue());
        assertFalse(new Value(-2).isGreaterThanOrEqualTo(new Value(-1)).booleanValue());
        assertFalse(new Value(-2.0).isGreaterThanOrEqualTo(new Value(-1.0)).booleanValue());
        assertTrue(new Value(-1.0).isGreaterThanOrEqualTo(new Value(-1.0)).booleanValue());
        assertFalse(new Value(-.02).isGreaterThanOrEqualTo(new Value(-.01)).booleanValue());
        assertTrue(new Value(-.01).isGreaterThanOrEqualTo(new Value(-.01)).booleanValue());

        assertFalse(new Value("a").isGreaterThanOrEqualTo(new Value("b")).booleanValue());
        assertTrue(new Value("a").isGreaterThanOrEqualTo(new Value("a")).booleanValue());
        assertFalse(new Value("apple").isGreaterThanOrEqualTo(new Value("banana")).booleanValue());
        assertTrue(new Value("apple").isGreaterThanOrEqualTo(new Value("apple")).booleanValue());
        assertFalse(new Value(10).isGreaterThanOrEqualTo(new Value("apple")).booleanValue());            // Lexical compare
        assertFalse(new Value(false).isGreaterThanOrEqualTo(new Value(true)).booleanValue());            // Lexical compare
        assertTrue(new Value(false).isGreaterThanOrEqualTo(new Value(false)).booleanValue());
    }

    @Test
    public void testLessThanOrEqualTo() {
        assertTrue(new Value(1).isLessThanOrEqualTo(new Value(1)).booleanValue());
        assertTrue(new Value(1).isLessThanOrEqualTo(new Value(2)).booleanValue());
        assertTrue(new Value(1.0).isLessThanOrEqualTo(new Value(1.0)).booleanValue());
        assertTrue(new Value(1.0).isLessThanOrEqualTo(new Value(2.0)).booleanValue());
        assertTrue(new Value(.01).isLessThanOrEqualTo(new Value(.01)).booleanValue());
        assertTrue(new Value(.01).isLessThanOrEqualTo(new Value(.02)).booleanValue());
        assertTrue(new Value(-1).isLessThanOrEqualTo(new Value(-1)).booleanValue());
        assertTrue(new Value(-2).isLessThanOrEqualTo(new Value(-1)).booleanValue());
        assertTrue(new Value(-2.0).isLessThanOrEqualTo(new Value(-1.0)).booleanValue());
        assertTrue(new Value(-1.0).isLessThanOrEqualTo(new Value(-1.0)).booleanValue());
        assertTrue(new Value(-.02).isLessThanOrEqualTo(new Value(-.01)).booleanValue());
        assertTrue(new Value(-.01).isLessThanOrEqualTo(new Value(-.01)).booleanValue());

        assertTrue(new Value("a").isLessThanOrEqualTo(new Value("b")).booleanValue());
        assertTrue(new Value("a").isLessThanOrEqualTo(new Value("a")).booleanValue());
        assertTrue(new Value("apple").isLessThanOrEqualTo(new Value("banana")).booleanValue());
        assertTrue(new Value("apple").isLessThanOrEqualTo(new Value("apple")).booleanValue());
        assertTrue(new Value(10).isLessThanOrEqualTo(new Value("apple")).booleanValue());            // Lexical compare
        assertTrue(new Value(false).isLessThanOrEqualTo(new Value(true)).booleanValue());            // Lexical compare
        assertTrue(new Value(false).isLessThanOrEqualTo(new Value(false)).booleanValue());
    }

    @Test
    public void testMultiply() throws Exception {
        assertEquals(new Value(200), new Value(10).multipliedBy(new Value(20)));
        assertEquals(new Value(-200), new Value(-10).multipliedBy(new Value(20)));
        assertEquals(new Value("200.0"), new Value(10.0).multipliedBy(new Value(20.0)));

        assertThrows(HtSemanticException.class, () -> new Value("a").multipliedBy(new Value("b")));
        assertThrows(HtSemanticException.class, () -> new Value(true).multipliedBy(new Value(false)));
        assertThrows(HtSemanticException.class, () -> new Value(new Point(1,2)).multipliedBy(new Value(new Point(3,4))));
        assertThrows(HtSemanticException.class, () -> new Value(new Rectangle(1,2, 3, 4)).multipliedBy(new Value(new Rectangle(3,4, 5, 6))));
    }

    @Test
    public void testDivide() throws Exception {
        assertEquals(new Value(2), new Value(20).dividedBy(new Value(10)));
        assertEquals(new Value(-.5), new Value(-10).dividedBy(new Value(20)));
        assertEquals(new Value("0.5"), new Value(10.0).dividedBy(new Value(20.0)));

        assertThrows(HtSemanticException.class, () -> new Value("a").dividedBy(new Value("b")));
        assertThrows(HtSemanticException.class, () -> new Value(true).dividedBy(new Value(false)));
        assertThrows(HtSemanticException.class, () -> new Value(new Point(1,2)).dividedBy(new Value(new Point(3,4))));
        assertThrows(HtSemanticException.class, () -> new Value(new Rectangle(1,2, 3, 4)).dividedBy(new Value(new Rectangle(3,4, 5, 6))));
    }

    @Test
    public void testAdd() throws Exception {
        assertEquals(new Value(30), new Value(20).add(new Value(10)));
        assertEquals(new Value(10), new Value(-10).add(new Value(20)));
        assertEquals(new Value("30.0"), new Value(10.0).add(new Value(20.0)));

        assertThrows(HtSemanticException.class, () -> new Value("a").add(new Value("b")));
        assertThrows(HtSemanticException.class, () -> new Value(true).add(new Value(false)));
        assertThrows(HtSemanticException.class, () -> new Value(new Point(1,2)).add(new Value(new Point(3,4))));
        assertThrows(HtSemanticException.class, () -> new Value(new Rectangle(1,2, 3, 4)).add(new Value(new Rectangle(3,4, 5, 6))));
    }

    @Test
    public void testSubtract() throws Exception {
        assertEquals(new Value(10), new Value(20).subtract(new Value(10)));
        assertEquals(new Value(-30), new Value(-10).subtract(new Value(20)));
        assertEquals(new Value("-10.0"), new Value(10.0).subtract(new Value(20.0)));

        assertThrows(HtSemanticException.class, () -> new Value("a").subtract(new Value("b")));
        assertThrows(HtSemanticException.class, () -> new Value(true).subtract(new Value(false)));
        assertThrows(HtSemanticException.class, () -> new Value(new Point(1,2)).subtract(new Value(new Point(3,4))));
        assertThrows(HtSemanticException.class, () -> new Value(new Rectangle(1,2, 3, 4)).subtract(new Value(new Rectangle(3,4, 5, 6))));
    }

    @Test
    public void testExponentiate() throws Exception {
        assertEquals(new Value(256), new Value(2).exponentiate(new Value(8)));
        assertEquals(new Value(256), new Value(-2).exponentiate(new Value(8)));
        assertEquals(new Value("256.0"), new Value(2.0).exponentiate(new Value(8.0)));

        assertThrows(HtSemanticException.class, () -> new Value("a").exponentiate(new Value("b")));
        assertThrows(HtSemanticException.class, () -> new Value(true).exponentiate(new Value(false)));
        assertThrows(HtSemanticException.class, () -> new Value(new Point(1,2)).exponentiate(new Value(new Point(3,4))));
        assertThrows(HtSemanticException.class, () -> new Value(new Rectangle(1,2, 3, 4)).exponentiate(new Value(new Rectangle(3,4, 5, 6))));
    }

    @Test
    public void testMod() throws Exception {
        assertEquals(new Value(1), new Value(10).mod(new Value(3)));
        assertEquals(new Value(-1), new Value(-10).mod(new Value(3)));
        assertEquals(new Value("1.0"), new Value(10.0).mod(new Value(3.0)));

        assertThrows(HtSemanticException.class, () -> new Value("a").mod(new Value("b")));
        assertThrows(HtSemanticException.class, () -> new Value(true).mod(new Value(false)));
        assertThrows(HtSemanticException.class, () -> new Value(new Point(1,2)).mod(new Value(new Point(3,4))));
        assertThrows(HtSemanticException.class, () -> new Value(new Rectangle(1,2, 3, 4)).mod(new Value(new Rectangle(3,4, 5, 6))));
    }

    @Test
    public void testNot() throws Exception {
        assertTrue(new Value(false).not().booleanValue());
        assertFalse(new Value(true).not(). booleanValue());

        assertThrows(HtSemanticException.class, () -> new Value(1).not());
        assertThrows(HtSemanticException.class, () -> new Value("a").not());
        assertThrows(HtSemanticException.class, () -> new Value(new Point(1,2)).not());
        assertThrows(HtSemanticException.class, () -> new Value(new Rectangle(1,2, 3, 4)).not());
    }

    @Test
    public void testNegate() throws Exception {
        assertEquals(new Value(-10), new Value(10).negate());
        assertEquals(new Value(20), new Value(-20).negate());

        assertThrows(HtSemanticException.class, () -> new Value(true).negate());
        assertThrows(HtSemanticException.class, () -> new Value("a").negate());
        assertThrows(HtSemanticException.class, () -> new Value(new Point(1,2)).negate());
        assertThrows(HtSemanticException.class, () -> new Value(new Rectangle(1,2, 3, 4)).negate());
    }

    @Test
    public void testAnd() throws Exception {
        assertTrue(new Value(true).and(new Value(true)).booleanValue());
        assertFalse(new Value(true).and(new Value(false)).booleanValue());
        assertFalse(new Value(false).and(new Value(true)).booleanValue());
        assertFalse(new Value(false).and(new Value(false)).booleanValue());

        assertThrows(HtSemanticException.class, () -> new Value(true).and(new Value("b")));
        assertThrows(HtSemanticException.class, () -> new Value(true).and(new Value(10)));
        assertThrows(HtSemanticException.class, () -> new Value(new Point(1,2)).and(new Value(false)));
        assertThrows(HtSemanticException.class, () -> new Value(new Rectangle(1,2, 3, 4)).and(new Value(true)));
    }

    @Test
    public void testOr() throws Exception {
        assertTrue(new Value(true).or(new Value(true)).booleanValue());
        assertTrue(new Value(true).or(new Value(false)).booleanValue());
        assertTrue(new Value(false).or(new Value(true)).booleanValue());
        assertFalse(new Value(false).or(new Value(false)).booleanValue());

        assertThrows(HtSemanticException.class, () -> new Value(true).or(new Value("b")));
        assertThrows(HtSemanticException.class, () -> new Value(true).or(new Value(10)));
        assertThrows(HtSemanticException.class, () -> new Value(new Point(1,2)).or(new Value(false)));
        assertThrows(HtSemanticException.class, () -> new Value(new Rectangle(1,2, 3, 4)).or(new Value(true)));
    }

    @Test
    public void testConcat() {
        assertEquals(new Value("ab"), new Value("a").concat(new Value("b")));
        assertEquals(new Value("12"), new Value(1).concat(new Value(2)));
        assertEquals(new Value("truefalse"), new Value(true).concat(new Value(false)));
        assertEquals(new Value("1,23,4"), new Value(new Point(1,2)).concat(new Value(new Point(3,4))));
    }

    @Test
    public void testWithin() throws Exception {
        assertTrue(new Value(1,1).isWithin(new Value("1,1,2,2")).booleanValue());
        assertTrue(new Value(10,10).isWithin(new Value("1,1,200,200")).booleanValue());
        assertFalse(new Value(2,2).isWithin(new Value("1,1,2,2")).booleanValue());
        assertFalse(new Value(-10,1).isWithin(new Value("1,1,2,2")).booleanValue());

        assertThrows(HtSemanticException.class, () -> new Value("barf").isWithin(new Value("1,2,3,4")));
        assertThrows(HtSemanticException.class, () -> new Value("barf").isWithin(new Value("1,2,3,4,5")));
        assertThrows(HtSemanticException.class, () -> new Value("1,2").isWithin(new Value("barf")));
    }

    @Test
    public void testTrunc() throws Exception {
        assertEquals(new Value(1), new Value(1.234).trunc());
        assertEquals(new Value(1), new Value(1).trunc());
        assertEquals(new Value(0), new Value(.234).trunc());
        assertEquals(new Value(-1), new Value(-1.234).trunc());
        assertEquals(new Value(-1), new Value(-1).trunc());
        assertEquals(new Value(0), new Value(-.234).trunc());

        assertThrows(HtSemanticException.class, () -> new Value(true).trunc());
        assertThrows(HtSemanticException.class, () -> new Value("barf").trunc());
    }

    @Test
    public void testIsALogical() throws Exception {
        assertTrue(new Value(true).isA(new Value("logical")).booleanValue());
        assertTrue(new Value(false).isA(new Value("bool")).booleanValue());
        assertTrue(new Value("false").isA(new Value("boolean")).booleanValue());

        assertFalse(new Value(10).isA(new Value("bool")).booleanValue());
        assertFalse(new Value("barf").isA(new Value("bool")).booleanValue());
    }

    @Test
    public void testIsADate() throws Exception {
        assertTrue(new Value("10/2/2036").isA(new Value("date")).booleanValue());                   // Short date
        assertTrue(new Value("10/2/96").isA(new Value("date")).booleanValue());                     // Short date
        assertTrue(new Value("239495838").isA(new Value("date")).booleanValue());                   // Seconds date
        assertTrue(new Value("Monday, January 1, 1985").isA(new Value("date")).booleanValue());     // Long date
        assertTrue(new Value("Mon, Jan 1, 1985").isA(new Value("date")).booleanValue());            // Abbrev date
        assertTrue(new Value("10:30:14 am").isA(new Value("date")).booleanValue());                 // Long time
        assertTrue(new Value("11:45 pm").isA(new Value("date")).booleanValue());                    // Short time
        assertTrue(new Value("2018,1,2,11,45,00,2").isA(new Value("date")).booleanValue());         // Date items

        assertFalse(new Value(1.25).isA(new Value("date")).booleanValue());
        assertFalse(new Value(true).isA(new Value("date")).booleanValue());
        assertFalse(new Value("barf").isA(new Value("date")).booleanValue());
    }

    @Test
    public void testIsARectangle() throws Exception {
        assertTrue(new Value(new Rectangle(1,2, 3, 4)).isA(new Value("rect")).booleanValue());
        assertTrue(new Value("-10, 20, 30, 45").isA(new Value("rectangle")).booleanValue());

        assertFalse(new Value("3.25, 2, 20, 30").isA(new Value("rect")).booleanValue());
        assertFalse(new Value(0).isA(new Value("rect")).booleanValue());
        assertFalse(new Value(1).isA(new Value("rect")).booleanValue());
        assertFalse(new Value(-1).isA(new Value("rect")).booleanValue());
        assertFalse(new Value(1.25).isA(new Value("rect")).booleanValue());
        assertFalse(new Value(true).isA(new Value("rect")).booleanValue());
        assertFalse(new Value("barf").isA(new Value("rect")).booleanValue());
        assertFalse(new Value("10/2/2036").isA(new Value("rect")).booleanValue());
    }

    @Test
    public void testIsAPoint() throws Exception {
        assertTrue(new Value(new Point(1,2)).isA(new Value("point")).booleanValue());
        assertTrue(new Value("-10, 20").isA(new Value("point")).booleanValue());

        assertFalse(new Value("3.25, 7.66").isA(new Value("point")).booleanValue());
        assertFalse(new Value(0).isA(new Value("point")).booleanValue());
        assertFalse(new Value(1).isA(new Value("point")).booleanValue());
        assertFalse(new Value(-1).isA(new Value("point")).booleanValue());
        assertFalse(new Value(1.25).isA(new Value("point")).booleanValue());
        assertFalse(new Value(true).isA(new Value("point")).booleanValue());
        assertFalse(new Value("barf").isA(new Value("point")).booleanValue());
        assertFalse(new Value(new Rectangle(1,2, 3, 4)).isA(new Value("point")).booleanValue());
        assertFalse(new Value("10/2/2036").isA(new Value("point")).booleanValue());
    }

    @Test
    public void testIsAnInteger() throws Exception {
        assertTrue(new Value(0).isA(new Value("integer")).booleanValue());
        assertTrue(new Value(1).isA(new Value("integer")).booleanValue());
        assertTrue(new Value(-1).isA(new Value("integer")).booleanValue());

        assertFalse(new Value(1.25).isA(new Value("integer")).booleanValue());
        assertFalse(new Value(true).isA(new Value("number")).booleanValue());
        assertFalse(new Value("barf").isA(new Value("number")).booleanValue());
        assertFalse(new Value(new Point(1,2)).isA(new Value("number")).booleanValue());
        assertFalse(new Value(new Rectangle(1,2, 3, 4)).isA(new Value("number")).booleanValue());
        assertFalse(new Value("10/2/2036").isA(new Value("number")).booleanValue());
    }

    @Test
    public void testIsANumber() throws Exception {
        assertTrue(new Value(0).isA(new Value("number")).booleanValue());
        assertTrue(new Value(1).isA(new Value("number")).booleanValue());
        assertTrue(new Value(-1).isA(new Value("number")).booleanValue());
        assertTrue(new Value(1.25).isA(new Value("number")).booleanValue());

        assertFalse(new Value(true).isA(new Value("number")).booleanValue());
        assertFalse(new Value("barf").isA(new Value("number")).booleanValue());
        assertFalse(new Value(new Point(1,2)).isA(new Value("number")).booleanValue());
        assertFalse(new Value(new Rectangle(1,2, 3, 4)).isA(new Value("number")).booleanValue());
        assertFalse(new Value("10/2/2036").isA(new Value("number")).booleanValue());
    }

    @Test
    public void testContains() {
        assertTrue(new Value().contains(new Value()));
        assertTrue(new Value("one two").contains(new Value("e t")));
        assertTrue(new Value("one two").contains(new Value("one two")));

        assertFalse(new Value("one two").contains(new Value("three")));
        assertFalse(new Value(new Point(1,2)).contains(new Value(new Point(3, 4))));
    }

    @Test
    public void testToString() {
        assertEquals("", new Value().toString());
        assertEquals("one", new Value("one").toString());
        assertEquals("2.25", new Value(2.25).toString());
        assertEquals("-0.57", new Value(-.57).toString());
        assertEquals("false", new Value(false).toString());
        assertEquals("true", new Value(true).toString());
        assertEquals("1,2", new Value(new Point(1,2)).toString());
        assertEquals("1,2,4,6", new Value(new Rectangle(1,2,3,4)).toString());
    }


    @Test
    public void testIntegerCompareTo() {
        assertEquals(0, new Value().compareTo(new Value()));
        assertEquals(-1, new Value(1).compareTo(new Value(2)));
        assertEquals(1, new Value(2).compareTo(new Value(1)));
        assertEquals(0, new Value(2).compareTo(new Value(2)));
    }

    @Test
    public void testFloatingCompareTo() {
        assertEquals(0, new Value().compareTo(new Value()));
        assertEquals(-1, new Value(.25).compareTo(new Value(1.25)));
        assertEquals(1, new Value(1.25).compareTo(new Value(.25)));
        assertEquals(0, new Value(.25).compareTo(new Value(.25)));
    }

    @Test
    public void testStringCompareTo() {
        assertEquals(0, new Value().compareTo(new Value()));
        assertEquals(-1, new Value("Apple").compareTo(new Value("Banana")));
        assertEquals(1, new Value("Banana").compareTo(new Value("Apple")));
        assertEquals(0, new Value("Apple").compareTo(new Value("Apple")));
    }

    @Test
    public void testStringStyledCompare() {
        assertEquals(0, new Value().compareTo(new Value(), SortStyle.TEXT));
        assertEquals(-1, new Value("Apple").compareTo(new Value("Banana"), SortStyle.TEXT));
        assertEquals(1, new Value("Banana").compareTo(new Value("Apple"), SortStyle.TEXT));
        assertEquals(0, new Value("Apple").compareTo(new Value("Apple"), SortStyle.TEXT));

        assertEquals(0, new Value().compareTo(new Value(), SortStyle.INTERNATIONAL));
        assertEquals(-1, new Value("Apple").compareTo(new Value("Banana"), SortStyle.INTERNATIONAL));
        assertEquals(1, new Value("Banana").compareTo(new Value("Apple"), SortStyle.INTERNATIONAL));
        assertEquals(0, new Value("Apple").compareTo(new Value("Apple"), SortStyle.INTERNATIONAL));
    }

    @Test
    public void testNumericStyledCompare() {
        assertEquals(0, new Value().compareTo(new Value(), SortStyle.NUMERIC));
        assertEquals(-1, new Value(.25).compareTo(new Value(1.25), SortStyle.NUMERIC));
        assertEquals(1, new Value(1.25).compareTo(new Value(.25), SortStyle.NUMERIC));
        assertEquals(0, new Value(.25).compareTo(new Value(.25), SortStyle.NUMERIC));
    }

    @Test
    public void testDateStyledCompare() {
        assertEquals(0, new Value().compareTo(new Value(), SortStyle.DATE_TIME));
        assertEquals(-1, new Value("1/1/900").compareTo(new Value("1/1/2000"), SortStyle.DATE_TIME));
        assertEquals(1, new Value("Monday, January 1, 2000").compareTo(new Value("Monday, December 31, 1999"), SortStyle.DATE_TIME));
        assertEquals(0, new Value("3:30 pm").compareTo(new Value("3:30:00 pm"), SortStyle.DATE_TIME));
    }

    @Test
    public void testOfQuotedLiteral() {
        assertTrue(Value.ofQuotedLiteral("blah").isQuotedLiteral());
        assertFalse(new Value("blah").isQuotedLiteral());
    }

    @Test
    public void testOfLines() {
        assertEquals(new Value(), Value.ofLines(Lists.newArrayList()));
        assertEquals(new Value("one"), Value.ofLines(Lists.newArrayList(new Value("one"))));
        assertEquals(new Value("one\ntwo"), Value.ofLines(Lists.newArrayList(new Value("one"), new Value("two"))));
        assertEquals(new Value("one\ntwo\n\nthree"), Value.ofLines(Lists.newArrayList(new Value("one"), new Value("two"), new Value(), new Value("three"))));
    }

    @Test
    public void testOfItems() {
        assertEquals(new Value(), Value.ofItems(Lists.newArrayList()));
        assertEquals(new Value("one"), Value.ofItems(Lists.newArrayList(new Value("one"))));
        assertEquals(new Value("one,two"), Value.ofItems(Lists.newArrayList(new Value("one"), new Value("two"))));
        assertEquals(new Value("one,two,,three"), Value.ofItems(Lists.newArrayList(new Value("one"), new Value("two"), new Value(), new Value("three"))));
    }

    @Test
    public void testOfWords() {
        assertEquals(new Value(), Value.ofWords(Lists.newArrayList()));
        assertEquals(new Value("one"), Value.ofWords(Lists.newArrayList(new Value("one"))));
        assertEquals(new Value("one two"), Value.ofWords(Lists.newArrayList(new Value("one"), new Value("two"))));
        assertEquals(new Value("one two  three"), Value.ofWords(Lists.newArrayList(new Value("one"), new Value("two"), new Value(), new Value("three"))));
    }

    @Test
    public void testOfChars() {
        assertEquals(new Value(), Value.ofChars(Lists.newArrayList()));
        assertEquals(new Value("1"), Value.ofChars(Lists.newArrayList(new Value("1"))));
        assertEquals(new Value("12"), Value.ofChars(Lists.newArrayList(new Value("1"), new Value("2"))));
        assertEquals(new Value("123"), Value.ofChars(Lists.newArrayList(new Value("1"), new Value("2"), new Value(), new Value("3"))));
    }

    @Test
    public void testOfMutatedChunk() throws Exception {

        assertEquals(new Value("one dos three"), Value.ofMutatedChunk(
                mockExecutionContext,
                new Value("one two three"),
                Preposition.INTO,
                TestChunkBuilder.buildSingleChunk(ChunkType.WORD, 2),
                new Value("dos")));

        assertEquals(new Value("onedos three"), Value.ofMutatedChunk(
                mockExecutionContext,
                new Value("one two three"),
                Preposition.REPLACING,
                TestChunkBuilder.buildSingleChunk(ChunkType.WORD, 2),
                new Value("dos")));

        assertEquals(new Value("one dos two three"), Value.ofMutatedChunk(
                mockExecutionContext,
                new Value("one two three"),
                Preposition.BEFORE,
                TestChunkBuilder.buildSingleChunk(ChunkType.WORD, 2),
                new Value("dos")));

        assertEquals(new Value("one two dos three"), Value.ofMutatedChunk(
                mockExecutionContext,
                new Value("one two three"),
                Preposition.AFTER,
                TestChunkBuilder.buildSingleChunk(ChunkType.WORD, 2),
                new Value("dos")));
    }

    @Test
    public void testOfValue() {
        assertEquals(new Value("that"), Value.ofValue(new Value("this"), Preposition.INTO, new Value("that")));
        assertEquals(new Value("that"), Value.ofValue(new Value("this"), Preposition.REPLACING, new Value("that")));
        assertEquals(new Value("thatthis"), Value.ofValue(new Value("this"), Preposition.BEFORE, new Value("that")));
        assertEquals(new Value("thisthat"), Value.ofValue(new Value("this"), Preposition.AFTER, new Value("that")));
    }
}
