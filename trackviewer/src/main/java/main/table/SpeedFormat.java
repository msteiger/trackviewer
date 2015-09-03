package main.table;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * Formats double values as speed string TODO: implement parseObject
 *
 * @author Martin Steiger
 */
public class SpeedFormat extends Format {

    private static final long serialVersionUID = 7985485800802181268L;

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        double val = (Double) obj;

        return toAppendTo.append(String.format("%.2f km/h", val));
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return null;
    }
}
