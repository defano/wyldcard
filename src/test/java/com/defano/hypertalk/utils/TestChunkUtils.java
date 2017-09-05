/*
 * TestChunkUtils
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.utils;

import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.containers.Preposition;
import com.defano.hypertalk.ast.common.ChunkType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestChunkUtils {

    @Test
    public void testPutAfterLine() {
        assertEquals("L1\nx\n\nL3\nL4", ChunkUtils.putChunk(ChunkType.LINE, Preposition.AFTER, "L1\n\nL3\nL4", 1, 0, "x"));
        assertEquals("L1\n\nx\nL3\nL4", ChunkUtils.putChunk(ChunkType.LINE, Preposition.AFTER, "L1\n\nL3\nL4", 2, 0, "x"));
        assertEquals("L1\n\nL3\nx\nL4", ChunkUtils.putChunk(ChunkType.LINE, Preposition.AFTER, "L1\n\nL3\nL4", 3, 0, "x"));
        assertEquals("L1\n\nL3\nL4\nx", ChunkUtils.putChunk(ChunkType.LINE, Preposition.AFTER, "L1\n\nL3\nL4", 4, 0, "x"));

        // Non-existent chunk
        assertEquals("L1\n\nL3\nL4\n\n\n\n\n\nx", ChunkUtils.putChunk(ChunkType.LINE, Preposition.AFTER, "L1\n\nL3\nL4", 9, 0, "x"));
    }


    @Test
    public void testPutBeforeLine() {
        assertEquals("x\nL1\n\nL3\nL4", ChunkUtils.putChunk(ChunkType.LINE, Preposition.BEFORE, "L1\n\nL3\nL4", 1, 0, "x"));
        assertEquals("L1\nx\n\nL3\nL4", ChunkUtils.putChunk(ChunkType.LINE, Preposition.BEFORE, "L1\n\nL3\nL4", 2, 0, "x"));
        assertEquals("L1\n\nx\nL3\nL4", ChunkUtils.putChunk(ChunkType.LINE, Preposition.BEFORE, "L1\n\nL3\nL4", 3, 0, "x"));
        assertEquals("L1\n\nL3\nx\nL4", ChunkUtils.putChunk(ChunkType.LINE, Preposition.BEFORE, "L1\n\nL3\nL4", 4, 0, "x"));

        // Non-existent chunk
        assertEquals("L1\n\nL3\nL4\n\n\n\n\nx\n", ChunkUtils.putChunk(ChunkType.LINE, Preposition.BEFORE, "L1\n\nL3\nL4", 9, 0, "x"));
    }

    @Test
    public void testPutIntoLine() {
        assertEquals("x\n\nL3\nL4", ChunkUtils.putChunk(ChunkType.LINE, Preposition.INTO, "L1\n\nL3\nL4", 1, 0, "x"));
        assertEquals("L1\nx\nL3\nL4", ChunkUtils.putChunk(ChunkType.LINE, Preposition.INTO, "L1\n\nL3\nL4", 2, 0, "x"));
        assertEquals("L1\n\nx\nL4", ChunkUtils.putChunk(ChunkType.LINE, Preposition.INTO, "L1\n\nL3\nL4", 3, 0, "x"));
        assertEquals("L1\n\nL3\nx", ChunkUtils.putChunk(ChunkType.LINE, Preposition.INTO, "L1\n\nL3\nL4", 4, 0, "x"));

        // Non-existent chunk
        assertEquals("L1\n\nL3\nL4\n\n\n\n\nx", ChunkUtils.putChunk(ChunkType.LINE, Preposition.INTO, "L1\n\nL3\nL4", 9, 0, "x"));
    }

    @Test
    public void testPutAfterItem() {
        assertEquals("1,x, 2,\n3,\t4", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.AFTER, "1, 2,\n3,\t4", 1, 0, "x"));
        assertEquals("1, 2,x,\n3,\t4", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.AFTER, "1, 2,\n3,\t4", 2, 0, "x"));
        assertEquals("1, 2,\n3,x,\t4", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.AFTER, "1, 2,\n3,\t4", 3, 0, "x"));
        assertEquals("1, 2,\n3,\t4,x", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.AFTER, "1, 2,\n3,\t4", 4, 0, "x"));

        // Non-existent chunk
        assertEquals("1, 2,\n3,\t4,,,,,,x", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.AFTER, "1, 2,\n3,\t4", 9, 0, "x"));
    }

    @Test
    public void testPutBeforeItem() {
        assertEquals("x,1, 2,\n3,\t4", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.BEFORE, "1, 2,\n3,\t4", 1, 0, "x"));
        assertEquals("1,x, 2,\n3,\t4", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.BEFORE, "1, 2,\n3,\t4", 2, 0, "x"));
        assertEquals("1, 2,x,\n3,\t4", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.BEFORE, "1, 2,\n3,\t4", 3, 0, "x"));
        assertEquals("1, 2,\n3,x,\t4", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.BEFORE, "1, 2,\n3,\t4", 4, 0, "x"));

        // Non-existent chunk
        assertEquals("1, 2,\n3,\t4,,,,,x,", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.BEFORE, "1, 2,\n3,\t4", 9, 0, "x"));
    }

    @Test
    public void testPutIntoItem() {
        assertEquals("x, 2,\n3,\t4", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4", 1, 0, "x"));
        assertEquals("1,x,\n3,\t4", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4", 2, 0, "x"));
        assertEquals("1, 2,x,\t4", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4", 3, 0, "x"));
        assertEquals("1, 2,\n3,x", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4", 4, 0, "x"));
        assertEquals("1, 2,\n3,\t4,x", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4,", 5, 0, "x"));

        // Non-existent chunk
        assertEquals("1, 2,\n3,\t4,,,,,x", ChunkUtils.putChunk(ChunkType.ITEM, Preposition.INTO, "1, 2,\n3,\t4", 9, 0, "x"));
    }

    @Test
    public void testPutIntoWord() {
        assertEquals("x  W2\nW3", ChunkUtils.putChunk(ChunkType.WORD, Preposition.INTO, "W1  W2\nW3", 1, 0, "x"));
        assertEquals("W1  x\nW3", ChunkUtils.putChunk(ChunkType.WORD, Preposition.INTO, "W1  W2\nW3", 2, 0, "x"));
        assertEquals("W1  W2\nx", ChunkUtils.putChunk(ChunkType.WORD, Preposition.INTO, "W1  W2\nW3", 3, 0, "x"));
    }

    @Test
    public void testPutBeforeWord() {
        assertEquals("x W1  W2\nW3", ChunkUtils.putChunk(ChunkType.WORD, Preposition.BEFORE, "W1  W2\nW3", 1, 0, "x"));
        assertEquals("W1  x W2\nW3", ChunkUtils.putChunk(ChunkType.WORD, Preposition.BEFORE, "W1  W2\nW3", 2, 0, "x"));
        assertEquals("W1  W2\nx W3", ChunkUtils.putChunk(ChunkType.WORD, Preposition.BEFORE, "W1  W2\nW3", 3, 0, "x"));
    }

    @Test
    public void testPutAfterWord() {
        assertEquals("W1 x  W2\nW3", ChunkUtils.putChunk(ChunkType.WORD, Preposition.AFTER, "W1  W2\nW3", 1, 0, "x"));
        assertEquals("W1  W2 x\nW3", ChunkUtils.putChunk(ChunkType.WORD, Preposition.AFTER, "W1  W2\nW3", 2, 0, "x"));
        assertEquals("W1  W2\nW3 x", ChunkUtils.putChunk(ChunkType.WORD, Preposition.AFTER, "W1  W2\nW3", 3, 0, "x"));
    }

    @Test
    public void testPutIntoChar() {
        // By integer
        assertEquals("xBCD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.INTO, "ABCD", 1, 0, "x"));
        assertEquals("AxCD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.INTO, "ABCD", 2, 0, "x"));
        assertEquals("ABxD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.INTO, "ABCD", 3, 0, "x"));
        assertEquals("ABCx", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.INTO, "ABCD", 4, 0, "x"));

        // By position
        assertEquals("xBCD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.INTO, "ABCD", Ordinal.FIRST.intValue(), 0, "x"));
        assertEquals("ABxD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.INTO, "ABCD", Ordinal.MIDDLE.intValue(), 0, "x"));
        assertEquals("ABCx", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.INTO, "ABCD", Ordinal.LAST.intValue(), 0, "x"));
    }

    @Test
    public void testPutBeforeChar() {
        // By integer
        assertEquals("xABCD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.BEFORE, "ABCD", 1, 0, "x"));
        assertEquals("AxBCD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.BEFORE, "ABCD", 2, 0, "x"));
        assertEquals("ABxCD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.BEFORE, "ABCD", 3, 0, "x"));
        assertEquals("ABCxD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.BEFORE, "ABCD", 4, 0, "x"));
    }

    @Test
    public void testPutAfterChar() {
        // By integer
        assertEquals("AxBCD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.AFTER, "ABCD", 1, 0, "x"));
        assertEquals("ABxCD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.AFTER, "ABCD", 2, 0, "x"));
        assertEquals("ABCxD", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.AFTER, "ABCD", 3, 0, "x"));
        assertEquals("ABCDx", ChunkUtils.putChunk(ChunkType.CHAR, Preposition.AFTER, "ABCD", 4, 0, "x"));
    }

    @Test
    public void testGetChar() {
        // By integer
        assertEquals("H", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", 1, 0));
        assertEquals("e", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", 2, 0));
        assertEquals("l", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", 3, 0));
        assertEquals("d", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", 11, 0));

        // By ordinal
        assertEquals("H", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.FIRST.intValue(), 0));
        assertEquals("e", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.SECOND.intValue(), 0));
        assertEquals("l", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.THIRD.intValue(), 0));
        assertEquals("l", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.FOURTH.intValue(), 0));
        assertEquals("o", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.FIFTH.intValue(), 0));
        assertEquals(" ", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.SIXTH.intValue(), 0));
        assertEquals("W", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.SEVENTH.intValue(), 0));
        assertEquals("o", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.EIGHTH.intValue(), 0));
        assertEquals("r", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.NINTH.intValue(), 0));
        assertEquals("l", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.TENTH.intValue(), 0));

        // By position
        assertEquals("d", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.LAST.intValue(), 0));
        assertEquals(" ", ChunkUtils.getChunk(ChunkType.CHAR, "Hello World", Ordinal.MIDDLE.intValue(), 0));
    }

    @Test
    public void testGetWord() {
        // By integer
        assertEquals("W1", ChunkUtils.getChunk(ChunkType.WORD, "W1  W2\t W3\n\n\nW4", 1, 0));
        assertEquals("W2", ChunkUtils.getChunk(ChunkType.WORD, "W1  W2\t W3\n\n\nW4", 2, 0));
        assertEquals("W3", ChunkUtils.getChunk(ChunkType.WORD, "W1  W2\t W3\n\n\nW4", 3, 0));
        assertEquals("W4", ChunkUtils.getChunk(ChunkType.WORD, "W1  W2\t W3\n\n\nW4", 4, 0));

        // By ordinal
        assertEquals("W2", ChunkUtils.getChunk(ChunkType.WORD, "W1 W2\t W3\nW4", Ordinal.SECOND.intValue(), 0));
        assertEquals("W3", ChunkUtils.getChunk(ChunkType.WORD, "W1 W2\t W3\nW4", Ordinal.THIRD.intValue(), 0));

        // By position
        assertEquals("W1", ChunkUtils.getChunk(ChunkType.WORD, "W1 W2\t W3\nW4", Ordinal.FIRST.intValue(), 0));
        assertEquals("W3", ChunkUtils.getChunk(ChunkType.WORD, "W1 W2\t W3\nW4", Ordinal.MIDDLE.intValue(), 0));
        assertEquals("W4", ChunkUtils.getChunk(ChunkType.WORD, "W1 W2\t W3\nW4", Ordinal.LAST.intValue(), 0));
    }

    @Test
    public void testGetLine() {
        // By integer
        assertEquals("L1", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", 1, 0));
        assertEquals("L2", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", 2, 0));
        assertEquals("", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", 3, 0));
        assertEquals("L4", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", 4, 0));
        assertEquals("L5", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", 5, 0));

        // By ordinal
        assertEquals("L2", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.SECOND.intValue(), 0));
        assertEquals("", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.THIRD.intValue(), 0));
        assertEquals("L4", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.FOURTH.intValue(), 0));
        assertEquals("L5", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.FIFTH.intValue(), 0));

        // By position
        assertEquals("L1", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.FIRST.intValue(), 0));
        assertEquals("L5", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.LAST.intValue(), 0));
        assertEquals("", ChunkUtils.getChunk(ChunkType.LINE, "L1\nL2\n\nL4\nL5", Ordinal.MIDDLE.intValue(), 0));
    }

    @Test
    public void testGetItem() {
        // By integer
        assertEquals("I1", ChunkUtils.getChunk(ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 1, 0));
        assertEquals("I2", ChunkUtils.getChunk(ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 2, 0));
        assertEquals(" I3  ", ChunkUtils.getChunk(ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 3, 0));
        assertEquals("I4\n", ChunkUtils.getChunk(ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 4, 0));
        assertEquals("I5", ChunkUtils.getChunk(ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 5, 0));
        assertEquals("", ChunkUtils.getChunk(ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 6, 0));
        assertEquals("", ChunkUtils.getChunk(ChunkType.ITEM, "I1,I2, I3  ,I4\n,I5,,", 7, 0));
        assertEquals("", ChunkUtils.getChunk(ChunkType.ITEM, ",", 1, 0));
        assertEquals("", ChunkUtils.getChunk(ChunkType.ITEM, ",", 2, 0));
        assertEquals("I1", ChunkUtils.getChunk(ChunkType.ITEM, "I1", 1, 0));

        assertEquals(0, ChunkUtils.getCount(ChunkType.ITEM, ""));
        assertEquals(1, ChunkUtils.getCount(ChunkType.ITEM, "1"));
        assertEquals(2, ChunkUtils.getCount(ChunkType.ITEM, "1,2"));
        assertEquals(3, ChunkUtils.getCount(ChunkType.ITEM, "1,2,"));
        assertEquals(4, ChunkUtils.getCount(ChunkType.ITEM, ",2,3,4"));

    }

}
