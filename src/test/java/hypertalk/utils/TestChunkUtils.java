package hypertalk.utils;

import hypertalk.ast.common.Ordinal;
import hypertalk.ast.containers.Preposition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestChunkUtils {

    @Test
    public void testPutAfterLine() {
        assertEquals("L1\nx\n\nL3\nL4", ChunkUtils.putLine(Preposition.AFTER, "L1\n\nL3\nL4", 1, "x"));
        assertEquals("L1\n\nx\nL3\nL4", ChunkUtils.putLine(Preposition.AFTER, "L1\n\nL3\nL4", 2, "x"));
        assertEquals("L1\n\nL3\nx\nL4", ChunkUtils.putLine(Preposition.AFTER, "L1\n\nL3\nL4", 3, "x"));
        assertEquals("L1\n\nL3\nL4\nx", ChunkUtils.putLine(Preposition.AFTER, "L1\n\nL3\nL4", 4, "x"));
    }


    @Test
    public void testPutBeforeLine() {
        assertEquals("x\nL1\n\nL3\nL4", ChunkUtils.putLine(Preposition.BEFORE, "L1\n\nL3\nL4", 1, "x"));
        assertEquals("L1\nx\n\nL3\nL4", ChunkUtils.putLine(Preposition.BEFORE, "L1\n\nL3\nL4", 2, "x"));
        assertEquals("L1\n\nx\nL3\nL4", ChunkUtils.putLine(Preposition.BEFORE, "L1\n\nL3\nL4", 3, "x"));
        assertEquals("L1\n\nL3\nx\nL4", ChunkUtils.putLine(Preposition.BEFORE, "L1\n\nL3\nL4", 4, "x"));
    }

    @Test
    public void testPutIntoLine() {
        assertEquals("x\n\nL3\nL4", ChunkUtils.putLine(Preposition.INTO, "L1\n\nL3\nL4", 1, "x"));
        assertEquals("L1\nx\nL3\nL4", ChunkUtils.putLine(Preposition.INTO, "L1\n\nL3\nL4", 2, "x"));
        assertEquals("L1\n\nx\nL4", ChunkUtils.putLine(Preposition.INTO, "L1\n\nL3\nL4", 3, "x"));
        assertEquals("L1\n\nL3\nx", ChunkUtils.putLine(Preposition.INTO, "L1\n\nL3\nL4", 4, "x"));
    }

    @Test
    public void testPutAfterItem() {
        assertEquals("1,x, 2,\n3,\t4", ChunkUtils.putItem(Preposition.AFTER, "1, 2,\n3,\t4", 1, "x"));
        assertEquals("1, 2,x,\n3,\t4", ChunkUtils.putItem(Preposition.AFTER, "1, 2,\n3,\t4", 2, "x"));
        assertEquals("1, 2,\n3,x,\t4", ChunkUtils.putItem(Preposition.AFTER, "1, 2,\n3,\t4", 3, "x"));
        assertEquals("1, 2,\n3,\t4,x", ChunkUtils.putItem(Preposition.AFTER, "1, 2,\n3,\t4", 4, "x"));
    }

    @Test
    public void testPutBeforeItem() {
        assertEquals("x,1, 2,\n3,\t4", ChunkUtils.putItem(Preposition.BEFORE, "1, 2,\n3,\t4", 1, "x"));
        assertEquals("1,x, 2,\n3,\t4", ChunkUtils.putItem(Preposition.BEFORE, "1, 2,\n3,\t4", 2, "x"));
        assertEquals("1, 2,x,\n3,\t4", ChunkUtils.putItem(Preposition.BEFORE, "1, 2,\n3,\t4", 3, "x"));
        assertEquals("1, 2,\n3,x,\t4", ChunkUtils.putItem(Preposition.BEFORE, "1, 2,\n3,\t4", 4, "x"));
    }

    @Test
    public void testPutIntoItem() {
        assertEquals("x, 2,\n3,\t4", ChunkUtils.putItem(Preposition.INTO, "1, 2,\n3,\t4", 1, "x"));
        assertEquals("1,x,\n3,\t4", ChunkUtils.putItem(Preposition.INTO, "1, 2,\n3,\t4", 2, "x"));
        assertEquals("1, 2,x,\t4", ChunkUtils.putItem(Preposition.INTO, "1, 2,\n3,\t4", 3, "x"));
        assertEquals("1, 2,\n3,x", ChunkUtils.putItem(Preposition.INTO, "1, 2,\n3,\t4", 4, "x"));
        assertEquals("1, 2,\n3,\t4,x", ChunkUtils.putItem(Preposition.INTO, "1, 2,\n3,\t4,", 5, "x"));
    }

    @Test
    public void testPutIntoWord() {
        assertEquals("x  W2\nW3", ChunkUtils.putWord(Preposition.INTO, "W1  W2\nW3", 1, "x"));
        assertEquals("W1  x\nW3", ChunkUtils.putWord(Preposition.INTO, "W1  W2\nW3", 2, "x"));
        assertEquals("W1  W2\nx", ChunkUtils.putWord(Preposition.INTO, "W1  W2\nW3", 3, "x"));
    }

    @Test
    public void testPutBeforeWord() {
        assertEquals("x W1  W2\nW3", ChunkUtils.putWord(Preposition.BEFORE, "W1  W2\nW3", 1, "x"));
        assertEquals("W1  x W2\nW3", ChunkUtils.putWord(Preposition.BEFORE, "W1  W2\nW3", 2, "x"));
        assertEquals("W1  W2\nx W3", ChunkUtils.putWord(Preposition.BEFORE, "W1  W2\nW3", 3, "x"));
    }

    @Test
    public void testPutAfterWord() {
        assertEquals("W1 x  W2\nW3", ChunkUtils.putWord(Preposition.AFTER, "W1  W2\nW3", 1, "x"));
        assertEquals("W1  W2 x\nW3", ChunkUtils.putWord(Preposition.AFTER, "W1  W2\nW3", 2, "x"));
        assertEquals("W1  W2\nW3 x", ChunkUtils.putWord(Preposition.AFTER, "W1  W2\nW3", 3, "x"));
    }

    @Test
    public void testPutIntoChar() {
        // By integer
        assertEquals("xBCD", ChunkUtils.putChar(Preposition.INTO, "ABCD", 1, "x"));
        assertEquals("AxCD", ChunkUtils.putChar(Preposition.INTO, "ABCD", 2, "x"));
        assertEquals("ABxD", ChunkUtils.putChar(Preposition.INTO, "ABCD", 3, "x"));
        assertEquals("ABCx", ChunkUtils.putChar(Preposition.INTO, "ABCD", 4, "x"));

        // By position
        assertEquals("xBCD", ChunkUtils.putChar(Preposition.INTO, "ABCD", Ordinal.FIRST.intValue(), "x"));
        assertEquals("ABxD", ChunkUtils.putChar(Preposition.INTO, "ABCD", Ordinal.MIDDLE.intValue(), "x"));
        assertEquals("ABCx", ChunkUtils.putChar(Preposition.INTO, "ABCD", Ordinal.LAST.intValue(), "x"));
    }

    @Test
    public void testPutBeforeChar() {
        // By integer
        assertEquals("xABCD", ChunkUtils.putChar(Preposition.BEFORE, "ABCD", 1, "x"));
        assertEquals("AxBCD", ChunkUtils.putChar(Preposition.BEFORE, "ABCD", 2, "x"));
        assertEquals("ABxCD", ChunkUtils.putChar(Preposition.BEFORE, "ABCD", 3, "x"));
        assertEquals("ABCxD", ChunkUtils.putChar(Preposition.BEFORE, "ABCD", 4, "x"));
    }

    @Test
    public void testPutAfterChar() {
        // By integer
        assertEquals("AxBCD", ChunkUtils.putChar(Preposition.AFTER, "ABCD", 1, "x"));
        assertEquals("ABxCD", ChunkUtils.putChar(Preposition.AFTER, "ABCD", 2, "x"));
        assertEquals("ABCxD", ChunkUtils.putChar(Preposition.AFTER, "ABCD", 3, "x"));
        assertEquals("ABCDx", ChunkUtils.putChar(Preposition.AFTER, "ABCD", 4, "x"));
    }

    @Test
    public void testGetChar() {
        // By integer
        assertEquals("H", ChunkUtils.getChar("Hello World", 1));
        assertEquals("e", ChunkUtils.getChar("Hello World", 2));
        assertEquals("l", ChunkUtils.getChar("Hello World", 3));
        assertEquals("d", ChunkUtils.getChar("Hello World", 11));

        // By ordinal
        assertEquals("H", ChunkUtils.getChar("Hello World", Ordinal.FIRST.intValue()));
        assertEquals("e", ChunkUtils.getChar("Hello World", Ordinal.SECOND.intValue()));
        assertEquals("l", ChunkUtils.getChar("Hello World", Ordinal.THIRD.intValue()));
        assertEquals("l", ChunkUtils.getChar("Hello World", Ordinal.FOURTH.intValue()));
        assertEquals("o", ChunkUtils.getChar("Hello World", Ordinal.FIFTH.intValue()));
        assertEquals(" ", ChunkUtils.getChar("Hello World", Ordinal.SIXTH.intValue()));
        assertEquals("W", ChunkUtils.getChar("Hello World", Ordinal.SEVENTH.intValue()));
        assertEquals("o", ChunkUtils.getChar("Hello World", Ordinal.EIGTH.intValue()));
        assertEquals("r", ChunkUtils.getChar("Hello World", Ordinal.NINTH.intValue()));
        assertEquals("l", ChunkUtils.getChar("Hello World", Ordinal.TENTH.intValue()));

        // By position
        assertEquals("d", ChunkUtils.getChar("Hello World", Ordinal.LAST.intValue()));
        assertEquals(" ", ChunkUtils.getChar("Hello World", Ordinal.MIDDLE.intValue()));
    }

    @Test
    public void testGetWord() {
        // By integer
        assertEquals("W1", ChunkUtils.getWord("W1  W2\t W3\n\n\nW4", 1));
        assertEquals("W2", ChunkUtils.getWord("W1  W2\t W3\n\n\nW4", 2));
        assertEquals("W3", ChunkUtils.getWord("W1  W2\t W3\n\n\nW4", 3));
        assertEquals("W4", ChunkUtils.getWord("W1  W2\t W3\n\n\nW4", 4));

        // By ordinal
        assertEquals("W2", ChunkUtils.getWord("W1 W2\t W3\nW4", Ordinal.SECOND.intValue()));
        assertEquals("W3", ChunkUtils.getWord("W1 W2\t W3\nW4", Ordinal.THIRD.intValue()));

        // By position
        assertEquals("W1", ChunkUtils.getWord("W1 W2\t W3\nW4", Ordinal.FIRST.intValue()));
        assertEquals("W3", ChunkUtils.getWord("W1 W2\t W3\nW4", Ordinal.MIDDLE.intValue()));
        assertEquals("W4", ChunkUtils.getWord("W1 W2\t W3\nW4", Ordinal.LAST.intValue()));
    }

    @Test
    public void testGetLine() {
        // By integer
        assertEquals("L1", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", 1));
        assertEquals("L2", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", 2));
        assertEquals("", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", 3));
        assertEquals("L4", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", 4));
        assertEquals("L5", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", 5));

        // By ordinal
        assertEquals("L2", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", Ordinal.SECOND.intValue()));
        assertEquals("", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", Ordinal.THIRD.intValue()));
        assertEquals("L4", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", Ordinal.FOURTH.intValue()));
        assertEquals("L5", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", Ordinal.FIFTH.intValue()));

        // By position
        assertEquals("L1", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", Ordinal.FIRST.intValue()));
        assertEquals("L5", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", Ordinal.LAST.intValue()));
        assertEquals("", ChunkUtils.getLine("L1\nL2\n\nL4\nL5", Ordinal.MIDDLE.intValue()));
    }

    @Test
    public void testGetItem() {
        // By integer
        assertEquals("I1", ChunkUtils.getItem("I1,I2, I3  ,I4\n,I5,,", 1));
        assertEquals("I2", ChunkUtils.getItem("I1,I2, I3  ,I4\n,I5,,", 2));
        assertEquals(" I3  ", ChunkUtils.getItem("I1,I2, I3  ,I4\n,I5,,", 3));
        assertEquals("I4\n", ChunkUtils.getItem("I1,I2, I3  ,I4\n,I5,,", 4));
        assertEquals("I5", ChunkUtils.getItem("I1,I2, I3  ,I4\n,I5,,", 5));
        assertEquals("", ChunkUtils.getItem("I1,I2, I3  ,I4\n,I5,,", 6));
        assertEquals("", ChunkUtils.getItem("I1,I2, I3  ,I4\n,I5,,", 7));
        assertEquals("", ChunkUtils.getItem(",", 1));
        assertEquals("", ChunkUtils.getItem(",", 2));
        assertEquals("I1", ChunkUtils.getItem("I1", 1));
    }

}
