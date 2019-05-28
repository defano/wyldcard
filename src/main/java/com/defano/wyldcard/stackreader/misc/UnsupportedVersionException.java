package com.defano.wyldcard.stackreader.misc;

import com.defano.wyldcard.stackreader.block.Block;

public class UnsupportedVersionException extends ImportException {
    public UnsupportedVersionException(Block block, String message) {
        super(block, message);
    }
}
