package com.defano.hypercard.runtime;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.IntStream;

/**
 * Enables case-insensitive language parsing without changing the actual script text or affecting literal
 * values (i.e., doesn't blindly convert all input to lowercase).
 *
 * Requires all tokens in the grammar to be lowercase (i.e., lexer rule 'mouseh' is correct, but 'mouseH' will
 * never match).
 */
public class CaseInsensitiveInputStream extends ANTLRInputStream {
    private char[] lowercase;

    CaseInsensitiveInputStream(String input) {
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
