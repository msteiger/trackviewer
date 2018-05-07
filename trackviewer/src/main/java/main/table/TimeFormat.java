package main.table;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Date;

/**
 * Formats Date values as time string
 *
 * @author Martin Steiger
 */
public class TimeFormat extends Format {

    private static final long serialVersionUID = -812583482882318040L;

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        long totSecs;
        if (obj instanceof Date) {
            totSecs = ((Date) obj).getTime() / 1000;
        } else if (obj instanceof Number) {
            totSecs = ((Number) obj).longValue() / 1000;
        } else {
            throw new IllegalArgumentException("TimeFormat expects Date or Long objects");
        }
        long sec = totSecs % 60;
        long min = (totSecs / 60) % 60;
        long hrs = (totSecs / 3600) % 60;

        if (hrs > 0) {
            toAppendTo.append(String.format("%d:%02d:%02d", hrs, min, sec));
        } else if (min > 0) {
            toAppendTo.append(String.format("%d:%02d", min, sec));
        } else {
            toAppendTo.append(String.format("%d", sec));
        }

        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        // not necessary

        return null;
    }

}
