package com.defano.hypercard.util;

public class StringUtils {

    public static String pluralize(long count, String singularFormat, String pluralFormat) {
        return Math.abs(count) == 1 ?
            String.format(singularFormat, count) :
            String.format(pluralFormat, count);
    }
}
