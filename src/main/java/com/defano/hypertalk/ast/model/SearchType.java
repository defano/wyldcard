package com.defano.hypertalk.ast.model;

public enum SearchType {

    /**
     * Whole or partial strings, including spaces, starting from the beginning of a word.
     */
    WHOLE,

    /**
     * Partial strings anywhere within a word.
     */
    CHARS,

    /**
     * Whole words only.
     */
    WORDS,

    /**
     * Partial strings anywhere, including spaces (ignores word boundaries).
     */
    STRING,

    /**
     * Whole or partial strings starting from the beginning of a word.
     */
    SUBSTRING
}
