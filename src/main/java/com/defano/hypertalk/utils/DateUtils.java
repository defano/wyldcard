package com.defano.hypertalk.utils;

import com.defano.hypertalk.ast.model.ConvertibleDateFormat;
import com.defano.hypertalk.ast.model.Convertible;
import com.defano.hypertalk.ast.model.Adjective;
import com.defano.hypertalk.ast.model.Value;

import java.text.*;
import java.util.Date;

public class DateUtils {

    public static Value valueOf(Date d, Adjective format) {
        switch (format) {
            case LONG:
                return new Value(ConvertibleDateFormat.LONG_DATE.dateFormat.format(d));
            case SHORT:
                return new Value(ConvertibleDateFormat.SHORT_DATE.dateFormat.format(d));
            case ABBREVIATED:
                return new Value(ConvertibleDateFormat.ABBREV_DATE.dateFormat.format(d));

            default:
                throw new IllegalArgumentException("Bug! Unimplemented date format.");
        }
    }

    public static Value valueOf(Date d, Convertible format) {
        DateFormat firstFormat = format.first.dateFormat;
        DateFormat secondFormat = format.second == null ? null : format.second.dateFormat;

        if (format.second != null) {
            return new Value(firstFormat.format(d) + " " + secondFormat.format(d));
        } else {
            return new Value(firstFormat.format(d));
        }
    }

    public static Date dateOf(Value value, Convertible format) {
        if (format == null) {
            return dateOf(value);
        }

        ParsePosition position = new ParsePosition(0);

        Date firstDate = dateOf(value.stringValue(), format.first, position);
        if (firstDate == null) {
            return null;
        }

        if (format.second != null) {
            Date secondDate = dateOf(value.stringValue(), format.second, position);
            return mergeDates(firstDate, secondDate, format.second);
        } else {
            return firstDate;
        }
    }

    public static Date dateOf(Value value, ConvertibleDateFormat format) {
        return dateOf(value.stringValue(), format, new ParsePosition(0));
    }

    public static Date dateOf(String text, ConvertibleDateFormat format, ParsePosition parsePosition) {
        try {
            switch (format) {
                case SECONDS:
                    return format.dateFormat.parse(text, parsePosition);
                case DATE_ITEMS:
                    return format.dateFormat.parse(text);
                case LONG_DATE:
                    return mergeDates(new Date(), format.dateFormat.parse(text, parsePosition), ConvertibleDateFormat.LONG_DATE);
                case SHORT_DATE:
                    return mergeDates(new Date(), format.dateFormat.parse(text, parsePosition), ConvertibleDateFormat.SHORT_DATE);
                case ABBREV_DATE:
                    return mergeDates(new Date(), format.dateFormat.parse(text, parsePosition), ConvertibleDateFormat.ABBREV_DATE);
                case LONG_TIME:
                    return mergeDates(new Date(), format.dateFormat.parse(text, parsePosition), ConvertibleDateFormat.LONG_TIME);
                case SHORT_TIME:
                    return mergeDates(new Date(), format.dateFormat.parse(text, parsePosition), ConvertibleDateFormat.SHORT_TIME);
            }
        } catch (ParseException e) {
            return null;
        }

        return null;
    }

    public static Date dateOf(Value value) {
        for (ConvertibleDateFormat thisFormat : ConvertibleDateFormat.values()) {
            Date d = dateOf(value, thisFormat);
            if (d != null) {
                return d;
            }
        }

        return null;
    }

    /**
     * Merges two dates together. Returns a date equal to first but with certain date fields overwritten with values
     * from second based on the given conversion format.
     *
     * @param first
     * @param second
     * @param secondFormat
     * @return
     */
    @SuppressWarnings("deprecation")
    private static Date mergeDates(Date first, Date second, ConvertibleDateFormat secondFormat) {
        if (first == null) {
            return null;
        }

        if (second == null || secondFormat == null) {
            return first;
        }

        Date updated = new Date(first.getTime());

        switch (secondFormat) {
            case SECONDS:
                return second;
            case DATE_ITEMS:
                return second;
            case LONG_DATE:
            case SHORT_DATE:
            case ABBREV_DATE:
                updated.setYear(second.getYear());
                updated.setMonth(second.getMonth());
                updated.setDate(second.getDate());
                return updated;

            case LONG_TIME:
            case SHORT_TIME:
                updated.setHours(second.getHours());
                updated.setMinutes(second.getMinutes());
                updated.setSeconds(second.getSeconds());
                return updated;
        }

        throw new IllegalStateException("Bug! Unimplemented conversion format: " + secondFormat);
    }

}
