
package main.table;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Date;

/**
 * Formats Date values as time string
 * TODO: implement parseObject
 * TODO: fix value where min or sec is only one digit long
 * @author Martin Steiger
 */
public class TimeFormat extends Format
{
	private static final long serialVersionUID = -812583482882318040L;

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
	{
		long totSecs = ((Date)obj).getTime() / 1000;
		long sec = totSecs % 60;
		long min = (totSecs / 60) % 60;
		long hrs = (totSecs / 3600) % 60;
		
		if (hrs > 0)
			toAppendTo.append(hrs + ":");
		
		if (hrs > 0 || min > 0)
		{
			toAppendTo.append(String.format("%02d:", min));
		}
		
		toAppendTo.append(String.format("%02d", sec));
		
		return toAppendTo;
	}

	@Override
	public Object parseObject(String source, ParsePosition pos)
	{
		return null;
	}

}
