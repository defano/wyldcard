/*
 * Range
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.utils;

import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.ChunkType;
import com.defano.hypertalk.ast.expressions.LiteralExp;

public class Range {
    public final int start, end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int length() {
        return end - start;
    }

    public Chunk asChunk() {
        return new Chunk(ChunkType.CHARRANGE, new LiteralExp(start + 1), new LiteralExp(end));
    }

    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public String toString() {
        return "Range{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
