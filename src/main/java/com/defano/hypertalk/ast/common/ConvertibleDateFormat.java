package com.defano.hypertalk.ast.common;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum ConvertibleDateFormat {
    
    SECONDS(new SecondsDateFormat()),
    DATE_ITEMS(new SimpleDateFormat("yyyy, MM, dd, H, mm, ss, F")),
    LONG_DATE(new SimpleDateFormat("EEEEE, MMMMM dd, yyyy")),
    SHORT_DATE(new SimpleDateFormat("MM/dd/yy")),
    ABBREV_DATE(new SimpleDateFormat("EEE, MMM dd, yyyy")),
    LONG_TIME(new SimpleDateFormat("h:mm:ss a")),
    SHORT_TIME(new SimpleDateFormat("h:mm a"));

    private final static int MS_PER_S = 1000;
    public final DateFormat dateFormat;

    ConvertibleDateFormat(DateFormat format) {
        this.dateFormat = format;
    }

    private static class SecondsDateFormat extends DateFormat {

        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            return toAppendTo.append(date.getTime() / MS_PER_S);
        }

        @Override
        public Date parse(String source, ParsePosition pos) {
            try {
                pos.setIndex(pos.getIndex() + source.length());
                return new Date(MS_PER_S * Long.parseLong(source));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

}
