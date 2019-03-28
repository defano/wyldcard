package com.defano.wyldcard.runtime.compiler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.IntStream;

/**
 * Enables case-insensitive language parsing without changing the actual script text or affecting literal
 * values (i.e., doesn't blindly convert all input to lowercase).
 *
 * Requires all tokens in the grammar (.g4 file) to be lowercase (i.e., lexer rule 'mouseh' is correct, but 'mouseH'
 * will never match).
 */
@SuppressWarnings("deprecation")
public class CaseInsensitiveInputStream extends ANTLRInputStream {
    private final char[] lowercase;

    public CaseInsensitiveInputStream(char[] input, int start, int length) {
        this(new String(input, start, length));
    }

    public CaseInsensitiveInputStream(String input) {
        super(input);
        this.lowercase = input.toLowerCase().toCharArray();
    }

    @Override
    public int LA(int i) {
        int data = super.LA(i);

        if (data == 0 || data == IntStream.EOF) {
            return data;
        } else {
            return lowercase[index() + i - 1];
        }
    }
}
