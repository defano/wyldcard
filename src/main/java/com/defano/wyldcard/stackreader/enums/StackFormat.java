package com.defano.wyldcard.stackreader.enums;

public enum StackFormat {

    /**
     * Not a HyperCard stack
     */
    INVALID(0),

    /**
     * Pre-release HyperCard 1.x stack
     */
    PRERELEASE_HYPERCARD_1(1,7),

    /**
     * HyperCard 1.x stack
     */
    HYPERCARD_1(8),

    /**
     * Pre-release HyperCard 2.x stack
     */
    PRERELEASE_HYPERCARD_2(9),

    /**
     * HyperCard 2.x stack
     */
    HYPERCARD_2(10);

    private final int minVer;
    private final int maxVer;

    StackFormat(int ver) {
        this.minVer = ver;
        this.maxVer = ver;
    }

    StackFormat(int minVer, int maxVer) {
        this.minVer = minVer;
        this.maxVer = maxVer;
    }

    public static StackFormat fromFormatInt(int formatId) {
        for (StackFormat thisFormat : values()) {
            if (formatId >= thisFormat.minVer && formatId <= thisFormat.maxVer) {
                return thisFormat;
            }
        }

        throw new IllegalArgumentException("Stack format id not recognized: " + formatId);
    }
}