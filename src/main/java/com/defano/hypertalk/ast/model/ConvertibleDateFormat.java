package com.defano.hypertalk.ast.model;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum ConvertibleDateFormat {
    
    SECONDS(new SecondsDateFormat()),
    DATE_ITEMS(new SimpleDateFormat("yyyy,M,d,H,mm,ss,F")),
    LONG_DATE(new SimpleDateFormat("EEEEE, MMMMM d, yyyy")),
    SHORT_DATE(new SimpleDateFormat("M/d/yy")),
    ABBREV_DATE(new SimpleDateFormat("EEE,MMM d,yyyy")),
    LONG_TIME(new SimpleDateFormat("h:mm:ss a")),
    SHORT_TIME(new SimpleDateFormat("h:mm a"));

    private final static int MS_PER_S = 1000;
    public final DateFormat dateFormat;

    ConvertibleDateFormat(DateFormat format) {
        this.dateFormat = format;
    }

    public static ConvertibleDateFormat ofDateLength(Adjective length) {
        switch (length) {
            case LONG:
                return LONG_DATE;
            case DEFAULT:
            case SHORT:
                return SHORT_DATE;
            case ABBREVIATED:
                return ABBREV_DATE;

            default: throw new IllegalArgumentException("Bug! Invalid date length.");
        }
    }

    public static ConvertibleDateFormat ofTimeLength(Adjective length) {
        switch (length) {
            case LONG:
                return LONG_TIME;
            case DEFAULT:
            case SHORT:
            case ABBREVIATED:
                return SHORT_TIME;

            default: throw new IllegalArgumentException("Bug! Invalid time length.");
        }
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
