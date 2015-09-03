
package main.table;

import java.text.Format;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * A {@link TableCellRenderer} that uses 
 * a given {@link Format} to format the string
 * @author Martin Steiger
 */
public class FormatRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 6232579188064229483L;

	private final Format formatter;

	/**
	 * @param formatter the specified formatter to format the Object 
	 */
	public FormatRenderer(Format formatter)
	{
		this(formatter, SwingConstants.RIGHT);
	}
	
	/**
	 * @param formatter the formatter
	 * @param alignment see {@link SwingConstants}
	 */
	public FormatRenderer(Format formatter, int alignment)
	{
		this.formatter = formatter;
		setHorizontalAlignment(alignment);
	}


	@Override
	public void setValue(Object value)
	{
		String v = null;
		try
		{
			v = formatter.format(value);
		}
		catch (IllegalArgumentException e)
		{
			v = String.valueOf(value);
		}
		super.setValue(v);
	}

}
