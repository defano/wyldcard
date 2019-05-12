package com.defano.wyldcard.stackreader.enums;

public enum PrintMethod {
    DRAFT,
    DEFERRED;

    public static PrintMethod fromMethodByte(byte method) {
        return method == 0 ? DRAFT : DEFERRED;
    }
}
