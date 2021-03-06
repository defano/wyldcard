package com.defano.wyldcard.stackreader.misc;

import com.defano.wyldcard.stackreader.block.Block;

public class ImportException extends Exception {

    private final Block block;

    public ImportException(Block source, String message) {
        super(message);
        this.block = source;
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
        this.block = null;
    }

    public ImportException(String message) {
        super(message);
        this.block = null;
    }

    public ImportException(Block source, String message, Throwable cause) {
        super(message, cause);
        this.block = source;
    }

    /**
     * Gets the block where this error originated when available.
     *
     * @return The block that caused the exception, or null when not available.
     */
    public Block getBlock() {
        return block;
    }
}
