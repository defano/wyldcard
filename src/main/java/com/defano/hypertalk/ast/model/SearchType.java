package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.exception.HtSemanticException;

public enum SearchType {

    /**
     * Whole or partial strings, including spaces, starting from the beginning of a word.
     */
    WHOLE("whole"),

    /**
     * Partial strings anywhere within a word.
     */
    CHARS("chars"),

    /**
     * Whole words only.
     */
    WORDS("word"),

    /**
     * Partial strings anywhere, including spaces (ignores word boundaries).
     */
    STRING("string");

    private final String hypertalkName;

    SearchType(String hypertalkName) {
        this.hypertalkName = hypertalkName;
    }

    public static SearchType fromHyperTalk(String searchType) throws HtSemanticException {
        for (SearchType thisType : values()) {
            if (thisType.hypertalkName.equalsIgnoreCase(searchType)) {
                return thisType;
            }
        }

        throw new HtSemanticException("No such search type.");
    }

}
