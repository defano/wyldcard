package com.defano.wyldcard.util;

import com.defano.hypertalk.ast.model.Value;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    private StringUtils() {
    }

    public static String pluralize(long count, String singularFormat, String pluralFormat) {
        return Math.abs(count) == 1 ?
            String.format(singularFormat, count) :
            String.format(pluralFormat, count);
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String humanReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int magnitude = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, magnitude)) + " " + units[magnitude];
    }

    public static List<Value> getValueList(List objects) {
        ArrayList<Value> values = new ArrayList<>();
        for (Object thisObject : objects) {
            values.add(new Value(String.valueOf(thisObject)));
        }
        return values;
    }
}
