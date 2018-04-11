package com.defano.hypertalk.utils;

import com.defano.hypertalk.ast.model.ChunkType;
import com.defano.hypertalk.ast.model.Ordinal;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestChunkUtils {

    private final ExecutionContext context = new ExecutionContext();

    @Test
    public void testDeleteChunks() throws HtSemanticException {
        assertEquals("L2\nL3", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.REPLACING, "L1\nL2\nL3", 1, 0, ""));
        assertEquals("L1\nL3", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.REPLACING, "L1\nL2\nL3", 2, 0, ""));
        assertEquals("L1\nL2", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.REPLACING, "L1\nL2\nL3", 3, 0, ""));

        assertEquals("L3", ChunkUtils.putChunk(context, ChunkType.LINERANGE, Preposition.REPLACING, "L1\nL2\nL3", 1, 2, ""));
        assertEquals("L1", ChunkUtils.putChunk(context, ChunkType.LINERANGE, Preposition.REPLACING, "L1\nL2\nL3", 2, 3, ""));
        assertEquals("", ChunkUtils.putChunk(context, ChunkType.LINERANGE, Preposition.REPLACING, "L1\nL2\nL3", 1, 3, ""));

        assertEquals("L2,L3", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.REPLACING, "L1,L2,L3", 1, 0, ""));
        assertEquals("L1,L3", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.REPLACING, "L1,L2,L3", 2, 0, ""));
        assertEquals("L1,L2", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.REPLACING, "L1,L2,L3", 3, 0, ""));

        assertEquals("L3", ChunkUtils.putChunk(context, ChunkType.ITEMRANGE, Preposition.REPLACING, "L1,L2,L3", 1, 2, ""));
        assertEquals("L1", ChunkUtils.putChunk(context, ChunkType.ITEMRANGE, Preposition.REPLACING, "L1,L2,L3", 2, 3, ""));
        assertEquals("", ChunkUtils.putChunk(context, ChunkType.ITEMRANGE, Preposition.REPLACING, "L1,L2,L3", 1, 3, ""));

        assertEquals("L2 L3", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.REPLACING, "L1 L2 L3", 1, 0, ""));
        assertEquals("L1 L3", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.REPLACING, "L1 L2 L3", 2, 0, ""));
        assertEquals("L1 L2", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.REPLACING, "L1 L2 L3", 3, 0, ""));

        assertEquals("L3", ChunkUtils.putChunk(context, ChunkType.WORDRANGE, Preposition.REPLACING, "L1 L2 L3", 1, 2, ""));
        assertEquals("L1", ChunkUtils.putChunk(context, ChunkType.WORDRANGE, Preposition.REPLACING, "L1 L2 L3", 2, 3, ""));
        assertEquals("", ChunkUtils.putChunk(context, ChunkType.WORDRANGE, Preposition.REPLACING, "L1 L2 L3", 1, 3, ""));

        assertEquals("23", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.REPLACING, "123", 1, 0, ""));
        assertEquals("13", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.REPLACING, "123", 2, 0, ""));
        assertEquals("12", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.REPLACING, "123", 3, 0, ""));

        assertEquals("3", ChunkUtils.putChunk(context, ChunkType.CHARRANGE, Preposition.REPLACING, "123", 1, 2, ""));
        assertEquals("1", ChunkUtils.putChunk(context, ChunkType.CHARRANGE, Preposition.REPLACING, "123", 2, 3, ""));
        assertEquals("", ChunkUtils.putChunk(context, ChunkType.CHARRANGE, Preposition.REPLACING, "123", 1, 3, ""));
    }

    @Test
    public void testPutAfterLine() throws HtSemanticException {
        assertEquals("L1\nx\n\nL3\nL4", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.AFTER, "L1\n\nL3\nL4", 1, 0, "x"));
        assertEquals("L1\n\nx\nL3\nL4", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.AFTER, "L1\n\nL3\nL4", 2, 0, "x"));
        assertEquals("L1\n\nL3\nx\nL4", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.AFTER, "L1\n\nL3\nL4", 3, 0, "x"));
        assertEquals("L1\n\nL3\nL4\nx", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.AFTER, "L1\n\nL3\nL4", 4, 0, "x"));

        // Non-existent chunk
        assertEquals("L1\n\nL3\nL4\n\n\n\n\n\nx", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.AFTER, "L1\n\nL3\nL4", 9, 0, "x"));
    }


    @Test
    public void testPutBeforeLine() throws HtSemanticException {
        assertEquals("x\nL1\n\nL3\nL4", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.BEFORE, "L1\n\nL3\nL4", 1, 0, "x"));
        assertEquals("L1\nx\n\nL3\nL4", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.BEFORE, "L1\n\nL3\nL4", 2, 0, "x"));
        assertEquals("L1\n\nx\nL3\nL4", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.BEFORE, "L1\n\nL3\nL4", 3, 0, "x"));
        assertEquals("L1\n\nL3\nx\nL4", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.BEFORE, "L1\n\nL3\nL4", 4, 0, "x"));

        // Non-existent chunk
        assertEquals("L1\n\nL3\nL4\n\n\n\n\nx\n", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.BEFORE, "L1\n\nL3\nL4", 9, 0, "x"));
    }

    @Test
    public void testPutIntoLine() throws HtSemanticException {
        assertEquals("x\n\nL3\nL4", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.INTO, "L1\n\nL3\nL4", 1, 0, "x"));
        assertEquals("L1\nx\nL3\nL4", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.INTO, "L1\n\nL3\nL4", 2, 0, "x"));
        assertEquals("L1\n\nx\nL4", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.INTO, "L1\n\nL3\nL4", 3, 0, "x"));
        assertEquals("L1\n\nL3\nx", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.INTO, "L1\n\nL3\nL4", 4, 0, "x"));

        // Non-existent chunk
        assertEquals("L1\n\nL3\nL4\n\n\n\n\nx", ChunkUtils.putChunk(context, ChunkType.LINE, Preposition.INTO, "L1\n\nL3\nL4", 9, 0, "x"));
    }

    @Test
    public void testPutAfterItem() throws HtSemanticException {
        assertEquals("1,x, 2,\n3,\t4", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.AFTER, "1, 2,\n3,\t4", 1, 0, "x"));
        assertEquals("1, 2,x,\n3,\t4", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.AFTER, "1, 2,\n3,\t4", 2, 0, "x"));
        assertEquals("1, 2,\n3,x,\t4", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.AFTER, "1, 2,\n3,\t4", 3, 0, "x"));
        assertEquals("1, 2,\n3,\t4,x", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.AFTER, "1, 2,\n3,\t4", 4, 0, "x"));

        // Non-existent chunk
        assertEquals("1, 2,\n3,\t4,,,,,,x", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.AFTER, "1, 2,\n3,\t4", 9, 0, "x"));
    }

    @Test
    public void testPutBeforeItem() throws HtSemanticException {
        assertEquals("x,1, 2,\n3,\t4", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.BEFORE, "1, 2,\n3,\t4", 1, 0, "x"));
        assertEquals("1,x, 2,\n3,\t4", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.BEFORE, "1, 2,\n3,\t4", 2, 0, "x"));
        assertEquals("1, 2,x,\n3,\t4", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.BEFORE, "1, 2,\n3,\t4", 3, 0, "x"));
        assertEquals("1, 2,\n3,x,\t4", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.BEFORE, "1, 2,\n3,\t4", 4, 0, "x"));

        // Non-existent chunk
        assertEquals("1, 2,\n3,\t4,,,,,x,", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.BEFORE, "1, 2,\n3,\t4", 9, 0, "x"));
    }

    @Test
    public void testPutIntoItem() throws HtSemanticException {
        assertEquals("x, 2,\n3,\t4", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4", 1, 0, "x"));
        assertEquals("1,x,\n3,\t4", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4", 2, 0, "x"));
        assertEquals("1, 2,x,\t4", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4", 3, 0, "x"));
        assertEquals("1, 2,\n3,x", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4", 4, 0, "x"));
        assertEquals("1, 2,\n3,\t4,x", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4,", 5, 0, "x"));

        // Non-existent chunk
        assertEquals("1, 2,\n3,\t4,,,,,x", ChunkUtils.putChunk(context, ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4", 9, 0, "x"));
    }

    @Test
    public void testPutIntoWord() throws HtSemanticException {
        assertEquals("x  W2\nW3", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.INTO, "W1  W2\nW3", 1, 0, "x"));
        assertEquals("W1  x\nW3", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.INTO, "W1  W2\nW3", 2, 0, "x"));
        assertEquals("W1  W2\nx", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.INTO, "W1  W2\nW3", 3, 0, "x"));
    }

    @Test
    public void testPutBeforeWord() throws HtSemanticException {
        assertEquals("x W1  W2\nW3", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.BEFORE, "W1  W2\nW3", 1, 0, "x"));
        assertEquals("W1  x W2\nW3", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.BEFORE, "W1  W2\nW3", 2, 0, "x"));
        assertEquals("W1  W2\nx W3", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.BEFORE, "W1  W2\nW3", 3, 0, "x"));
    }

    @Test
    public void testPutAfterWord() throws HtSemanticException {
        assertEquals("W1 x  W2\nW3", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.AFTER, "W1  W2\nW3", 1, 0, "x"));
        assertEquals("W1  W2 x\nW3", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.AFTER, "W1  W2\nW3", 2, 0, "x"));
        assertEquals("W1  W2\nW3 x", ChunkUtils.putChunk(context, ChunkType.WORD, Preposition.AFTER, "W1  W2\nW3", 3, 0, "x"));
    }

    @Test
    public void testPutIntoCharRange() throws HtSemanticException {
        assertEquals("xCD", ChunkUtils.putChunk(context, ChunkType.CHARRANGE, Preposition.INTO, "ABCD", 1, 2, "x"));
        assertEquals("AxxD", ChunkUtils.putChunk(context, ChunkType.CHARRANGE, Preposition.INTO, "ABCD", 2, 3, "xx"));
        assertEquals("ABxx", ChunkUtils.putChunk(context, ChunkType.CHARRANGE, Preposition.INTO, "ABCD", 3, 4, "xx"));
        assertEquals("xxxx", ChunkUtils.putChunk(context, ChunkType.CHARRANGE, Preposition.INTO, "ABCD", 1, 4, "xxxx"));
        assertEquals("One\n2\nThree", ChunkUtils.putChunk(context, ChunkType.CHARRANGE, Preposition.INTO, "One\nTwo\nThree", 5, 7, "2"));
    }

    @Test
    public void testPutIntoChar() throws HtSemanticException {
        // By integer
        assertEquals("xBCD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.INTO, "ABCD", 1, 0, "x"));
        assertEquals("AxCD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.INTO, "ABCD", 2, 0, "x"));
        assertEquals("ABxD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.INTO, "ABCD", 3, 0, "x"));
        assertEquals("ABCx", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.INTO, "ABCD", 4, 0, "x"));

        // Multiline
        assertEquals("AB\nCx", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.INTO, "AB\nCD", 5, 0, "x"));


        // By position
        assertEquals("xBCD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.INTO, "ABCD", Ordinal.FIRST.intValue(), 0, "x"));
        assertEquals("ABxD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.INTO, "ABCD", Ordinal.MIDDLE.intValue(), 0, "x"));
        assertEquals("ABCx", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.INTO, "ABCD", Ordinal.LAST.intValue(), 0, "x"));
    }

    @Test
    public void testPutBeforeChar() throws HtSemanticException {
        // By integer
        assertEquals("xABCD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.BEFORE, "ABCD", 1, 0, "x"));
        assertEquals("AxBCD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.BEFORE, "ABCD", 2, 0, "x"));
        assertEquals("ABxCD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.BEFORE, "ABCD", 3, 0, "x"));
        assertEquals("ABCxD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.BEFORE, "ABCD", 4, 0, "x"));
    }

    @Test
    public void testPutAfterChar() throws HtSemanticException {
        // By integer
        assertEquals("AxBCD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.AFTER, "ABCD", 1, 0, "x"));
        assertEquals("ABxCD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.AFTER, "ABCD", 2, 0, "x"));
        assertEquals("ABCxD", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.AFTER, "ABCD", 3, 0, "x"));
        assertEquals("ABCDx", ChunkUtils.putChunk(context, ChunkType.CHAR, Preposition.AFTER, "ABCD", 4, 0, "x"));
    }

    @Test
    public void testGetChar() {
        // By integer
        assertEquals("H", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", 1, 0));
        assertEquals("e", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", 2, 0));
        assertEquals("l", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", 3, 0));
        assertEquals("d", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", 11, 0));

        // By ordinal
        assertEquals("H", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.FIRST.intValue(), 0));
        assertEquals("e", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.SECOND.intValue(), 0));
        assertEquals("l", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.THIRD.intValue(), 0));
        assertEquals("l", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.FOURTH.intValue(), 0));
        assertEquals("o", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.FIFTH.intValue(), 0));
        assertEquals(" ", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.SIXTH.intValue(), 0));
        assertEquals("W", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.SEVENTH.intValue(), 0));
        assertEquals("o", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.EIGHTH.intValue(), 0));
        assertEquals("r", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.NINTH.intValue(), 0));
        assertEquals("l", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.TENTH.intValue(), 0));

        // By position
        assertEquals("d", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.LAST.intValue(), 0));
        assertEquals(" ", ChunkUtils.getChunk(context, ChunkType.CHAR, "Hello World", Ordinal.MIDDLE.intValue(), 0));
    }

    @Test
    public void testGetWord() {
        // Quoted words
        assertEquals("\"W1\"", ChunkUtils.getChunk(context, ChunkType.WORD, "\"W1\"  \"W2\"\t \"W3\"\n\n\n\"W4\"", 1, 0));
        assertEquals("\'W1\'", ChunkUtils.getChunk(context, ChunkType.WORD, "\'W1\'  \'W2\'\t \'W3\'\n\n\n\'W4\'", 1, 0));

        // Words without letters
        assertEquals("@#$", ChunkUtils.getChunk(context, ChunkType.WORD, "@#$  %^\t &*\n\n\n()", 1, 0));
        assertEquals("%^", ChunkUtils.getChunk(context, ChunkType.WORD, "@#$  %^\t &*\n\n\n()", 2, 0));
        assertEquals("&*", ChunkUtils.getChunk(context, ChunkType.WORD, "@#$  %^\t &*\n\n\n()", 3, 0));
        assertEquals("()", ChunkUtils.getChunk(context, ChunkType.WORD, "@#$  %^\t &*\n\n\n()", 4, 0));

        // By integer
        assertEquals("W1", ChunkUtils.getChunk(context, ChunkType.WORD, "W1  W2\t W3\n\n\nW4", 1, 0));
        assertEquals("W2", ChunkUtils.getChunk(context, ChunkType.WORD, "W1  W2\t W3\n\n\nW4", 2, 0));
        assertEquals("W3", ChunkUtils.getChunk(context, ChunkType.WORD, "W1  W2\t W3\n\n\nW4", 3, 0));
        assertEquals("W4", ChunkUtils.getChunk(context, ChunkType.WORD, "W1  W2\t W3\n\n\nW4", 4, 0));

        // By ordinal
        assertEquals("W2", ChunkUtils.getChunk(context, ChunkType.WORD, "W1 W2\t W3\nW4", Ordinal.SECOND.intValue(), 0));
        assertEquals("W3", ChunkUtils.getChunk(context, ChunkType.WORD, "W1 W2\t W3\nW4", Ordinal.THIRD.intValue(), 0));

        // By position
        assertEquals("W1", ChunkUtils.getChunk(context, ChunkType.WORD, "W1 W2\t W3\nW4", Ordinal.FIRST.intValue(), 0));
        assertEquals("W3", ChunkUtils.getChunk(context, ChunkType.WORD, "W1 W2\t W3\nW4", Ordinal.MIDDLE.intValue(), 0));
        assertEquals("W4", ChunkUtils.getChunk(context, ChunkType.WORD, "W1 W2\t W3\nW4", Ordinal.LAST.intValue(), 0));
    }

    @Test
    public void testGetLine() {
        // By integer
        assertEquals("L1", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", 1, 0));
        assertEquals("L2", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", 2, 0));
        assertEquals("", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", 3, 0));
        assertEquals("L4", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", 4, 0));
        assertEquals("L5", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", 5, 0));

        // By ordinal
        assertEquals("L2", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.SECOND.intValue(), 0));
        assertEquals("", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.THIRD.intValue(), 0));
        assertEquals("L4", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.FOURTH.intValue(), 0));
        assertEquals("L5", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.FIFTH.intValue(), 0));

        // By position
        assertEquals("L1", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.FIRST.intValue(), 0));
        assertEquals("L5", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.LAST.intValue(), 0));
        assertEquals("", ChunkUtils.getChunk(context, ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.MIDDLE.intValue(), 0));
    }

    @Test
    public void testGetItem() {
        // By integer
        assertEquals("I1", ChunkUtils.getChunk(context, ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 1, 0));
        assertEquals("I2", ChunkUtils.getChunk(context, ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 2, 0));
        assertEquals(" I3  ", ChunkUtils.getChunk(context, ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 3, 0));
        assertEquals("I4\n", ChunkUtils.getChunk(context, ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 4, 0));
        assertEquals("I5", ChunkUtils.getChunk(context, ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 5, 0));
        assertEquals("", ChunkUtils.getChunk(context, ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 6, 0));
        assertEquals("", ChunkUtils.getChunk(context, ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 7, 0));
        assertEquals("", ChunkUtils.getChunk(context, ChunkType.ITEM, ",", 1, 0));
        assertEquals("", ChunkUtils.getChunk(context, ChunkType.ITEM, ",", 2, 0));
        assertEquals("I1", ChunkUtils.getChunk(context, ChunkType.ITEM, "I1", 1, 0));

        assertEquals(0, ChunkUtils.getCount(context, ChunkType.ITEM, ""));
        assertEquals(1, ChunkUtils.getCount(context, ChunkType.ITEM, "1"));
        assertEquals(2, ChunkUtils.getCount(context, ChunkType.ITEM, "1,2"));
        assertEquals(3, ChunkUtils.getCount(context, ChunkType.ITEM, "1,2,"));
        assertEquals(4, ChunkUtils.getCount(context, ChunkType.ITEM, ",2,3,4"));

    }

}
