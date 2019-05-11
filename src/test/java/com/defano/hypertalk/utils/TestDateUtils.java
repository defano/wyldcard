package com.defano.hypertalk.utils;

import com.defano.hypertalk.ast.model.Convertible;
import com.defano.hypertalk.ast.model.enums.ConvertibleDateFormat;
import com.defano.hypertalk.ast.model.Value;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TestDateUtils {

    @Test
    public void testSingleFormatIdentityConversion() {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        Date now = c.getTime();

        Value v;

        v = DateUtils.valueOf(now, new Convertible(ConvertibleDateFormat.SECONDS));
        assertDateTimeSecond(now, DateUtils.dateOf(v, new Convertible(ConvertibleDateFormat.SECONDS)));
        assertDateTimeSecond(now, DateUtils.dateOf(v));

        v = DateUtils.valueOf(now, new Convertible(ConvertibleDateFormat.DATE_ITEMS));
        assertDateTimeSecond(now, DateUtils.dateOf(v, new Convertible(ConvertibleDateFormat.DATE_ITEMS)));
        assertDateTimeSecond(now, DateUtils.dateOf(v));

        v = DateUtils.valueOf(now, new Convertible(ConvertibleDateFormat.LONG_DATE));
        assertDate(now, DateUtils.dateOf(v, new Convertible(ConvertibleDateFormat.LONG_DATE)));
        assertDate(now, DateUtils.dateOf(v));

        v = DateUtils.valueOf(now, new Convertible(ConvertibleDateFormat.SHORT_DATE));
        assertDate(now, DateUtils.dateOf(v, new Convertible(ConvertibleDateFormat.SHORT_DATE)));
        assertDate(now, DateUtils.dateOf(v));

        v = DateUtils.valueOf(now, new Convertible(ConvertibleDateFormat.ABBREV_DATE));
        assertDate(now, DateUtils.dateOf(v, new Convertible(ConvertibleDateFormat.ABBREV_DATE)));
        assertDate(now, DateUtils.dateOf(v));

        v = DateUtils.valueOf(now, new Convertible(ConvertibleDateFormat.LONG_TIME));
        assertDateTimeSecond(now, DateUtils.dateOf(v, new Convertible(ConvertibleDateFormat.LONG_TIME)));
        assertDateTimeSecond(now, DateUtils.dateOf(v));

        v = DateUtils.valueOf(now, new Convertible(ConvertibleDateFormat.SHORT_TIME));
        assertDateTime(now, DateUtils.dateOf(v, new Convertible(ConvertibleDateFormat.SHORT_TIME)));
        assertDateTime(now, DateUtils.dateOf(v));

    }

    @SuppressWarnings("deprecation")
    private void assertDate(Date d1, Date d2) {
        assertEquals(d1.getYear(), d2.getYear());
        assertEquals(d1.getMonth(), d2.getMonth());
        assertEquals(d1.getDate(), d2.getDate());
    }

    @SuppressWarnings("deprecation")
    private void assertDateTime(Date d1, Date d2) {
        assertDate(d1, d2);
        assertEquals(d1.getHours(), d2.getHours());
        assertEquals(d1.getMinutes(), d2.getMinutes());
    }

    @SuppressWarnings("deprecation")
    private void assertDateTimeSecond(Date d1, Date d2) {
        assertDateTime(d1, d2);
        assertEquals(d1.getSeconds(), d2.getSeconds());
    }

}
