package com.defano.wyldcard.importer.type;

import java.util.ArrayList;

public enum  StackFlag {
    CANT_MODIFY(0x80),
    CANT_DELETE(0x40),
    PRIVATE_ACCESS(0x20),
    CANT_ABORT(0x08),
    CANT_PEEK(0x04);

    int bitmask;

    StackFlag(int bitmask) {
        this.bitmask = bitmask;
    }

    public static StackFlag[] fromBitmask(int bitmask) {
        ArrayList<StackFlag> flags = new ArrayList<>();

        for (StackFlag thisFlag : values()) {
            if ((thisFlag.bitmask & bitmask) != 0) {
                flags.add(thisFlag);
            }
        }

        return flags.toArray(new StackFlag[] {});
    }
}