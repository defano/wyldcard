package com.defano.hypertalk.ast.model.enums;

public enum Preposition {
    /**
     * Refers to the chunk directly preceding the specified chunk.
     */
    BEFORE,

    /**
     * Refers to the chunk directly following the specified chunk.
     */
    AFTER,

    /**
     * Refers to the specified chunk not including its delimiter.
     */
    INTO,

    /**
     * Refers to the specified chunk and its delimiter (preceding delimiter if not the first chunk;
     * trailing delimiter if the first chunk.
     */
    REPLACING
}
