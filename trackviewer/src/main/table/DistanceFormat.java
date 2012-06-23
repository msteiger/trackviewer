
package main.table;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * Formats double values as distance string
 * TODO: implement parseObject
 * @author Martin Steiger
 */
public class DistanceFormat extends Format
{
	private static final long serialVersionUID = 7985485800802181268L;

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
	{
		double val = (Double)obj;

		return toAppendTo.append(String.format("%.2fkm", val * 0.001));
	}

	@Override
	public Object parseObject(String source, ParsePosition pos)
	{
		return null;
	}
}