
package main.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class JChart extends JComponent
{
	private Double boundHigh;
	private Double boundLow;
	private String horzDesc;
	private String vertDesc;

	private final List<List<Point2D>> series = new ArrayList<List<Point2D>>();
	private final Rectangle chartRect  = new Rectangle();
	private final Font _fontLegend = this.getFont();
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (series.isEmpty())
			return;
		
		updateChartRect();

		drawHorzGrid(g);
		drawVertGrid(g);
		
		for (List<Point2D> s : series)
		{
			drawSeries(s, (Graphics2D)g);
		}
	}

	private void updateChartRect()
	{
		int width = this.getWidth();
		int height = getHeight();

		int labelWidth = 35;
		int labelHeight = 30;
		
		Insets insets = getInsets();

		int px1 = insets.left + labelWidth;
		int py1 = insets.top + labelHeight;
		int px2 = insets.right + labelWidth;
		int py2 = insets.bottom + labelHeight;

		chartRect.setRect(px1, py1, width - px1 - px2, height - py1 - py2);
	}
	
	private void drawVertGrid(Graphics g)
	{
		final double pad_text = 5.0f;
		final double pad_indi = 3.0f;
        final double overlap = 3.0f;

        final int arrowSize = 3;

		double val = boundLow.getY();
		double range = boundHigh.getY() - boundLow.getY();
		double multi = findMultiplier(chartRect.getHeight(), range, 30.0f);

		DecimalFormat df = new DecimalFormat("#.##");

		do
		{
			int pos = (int) (chartRect.getMaxY() - ((val - boundLow.getY()) / range) * chartRect.getHeight());

			String str = df.format(val);
			Rectangle2D size = g.getFontMetrics().getStringBounds(str, g);

			int x = (int) (chartRect.getMinX() - size.getWidth() - pad_text);
			int y = pos;

			g.setColor(Color.BLACK);
			g.drawString(str, x, y);

			g.setColor(Color.LIGHT_GRAY);
			g.drawLine((int)(chartRect.getMinX() - pad_indi), pos, (int)(chartRect.getMaxX() + overlap), pos);

			val += multi;
		}
		while (val <= boundHigh.getY());

		// Draw horizontal arrow
		int arrowOut = 5;
		int x = (int) (chartRect.getMinX());
		int y = (int) (chartRect.getMinY() - overlap - arrowOut);
		g.setColor(Color.BLACK);
		g.drawLine(x, y, x, (int)chartRect.getMaxY());
		g.drawLine(x, y, x - arrowSize, y + arrowSize);
		g.drawLine(x, y, x + arrowSize, y + arrowSize);

		// Draw vertical axis description
		Rectangle2D descSize = g.getFontMetrics().getStringBounds(vertDesc, g);
		x = (int) (chartRect.getMinX() - descSize.getWidth() * 0.5);
		y = (int) (chartRect.getMinY() - overlap - arrowOut - 2 * arrowSize);
		g.drawString(vertDesc, x, y); 
	}

	private void drawHorzGrid(Graphics g)
	{
		g.setFont(_fontLegend);
		g.setColor(Color.BLACK);
		
		final int pad_text = 5;
		final int pad_indi = 3;
        final int arrowSize = 3;
        final int overlap = 3;

		double range = boundHigh.getX() - boundLow.getX();
		double multi = findMultiplier(chartRect.getWidth(), range, 30.0f);
		double val = boundLow.getX();

		DecimalFormat df = new DecimalFormat("#.##");

		do
		{
			int pos = (int) (chartRect.getMinX() + ((val - boundLow.getX()) / range) * chartRect.getWidth());

			String str = df.format(val);
			Rectangle2D size = g.getFontMetrics().getStringBounds(str, g);

			int x = (int) (pos - size.getWidth() * 0.5f);
			int y = (int) (chartRect.getMaxY() + size.getHeight() + pad_text);

			g.setColor(Color.BLACK);
			g.drawString(str, x, y);
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(pos, (int)chartRect.getMinY() - overlap, pos, (int)(chartRect.getMaxY() + pad_indi));

			val += multi;
		}
		while (val <= boundHigh.getX());

		// Draw right grid line
		g.drawLine((int)chartRect.getMaxX(), (int)chartRect.getMinY()- overlap, (int)chartRect.getMaxX(), (int)(chartRect.getMaxY() + pad_indi));

		// Draw horizontal arrow
		int arrowOut = 5;
		int x = (int) (chartRect.getMaxX() + overlap + arrowOut);
		int y = (int) chartRect.getMaxY();
		g.setColor(Color.BLACK);
		g.drawLine(x, y, (int)chartRect.getMinX(), y);
		g.drawLine(x, y, x - arrowSize, y - arrowSize);
		g.drawLine(x, y, x - arrowSize, y + arrowSize);

		// Draw horizontal axis description
		x = (int) (chartRect.getMaxX() + overlap + arrowSize * 3);
		y = (int) (chartRect.getMaxY() + g.getFontMetrics().getAscent() - 1);
		g.drawString(horzDesc, x, y);
	}
	
	/**
	 * @param data a list of chart series data (it is not copied)
	 */
	public void setData(List<List<Point2D>> data)
	{
		series.clear();

		if (data.isEmpty())
			return;
		
		series.addAll(data);		// does not copy the content

		Rectangle2D bounds = null;
		
		for (List<Point2D> list : data)
		{
			Rectangle2D rc = computeBounds(list);
			
			if (bounds == null)
				bounds = rc; else
				Rectangle2D.union(bounds, rc, bounds);
		}

		Point2D rangeY = roundRange(bounds.getMinY(), bounds.getMaxY());
		
		boundLow  = new Point2D.Double(bounds.getMinX(), rangeY.getX());
		boundHigh = new Point2D.Double(bounds.getMaxX(), rangeY.getY());

		repaint();
	}
	
	private Point2D roundRange(double val_min, double val_max)
	{
		double rnd_exp;

		rnd_exp = Math.floor(Math.log10(val_max));			// transform to format #.##### * 10 ^ (rnd_exp)

		double rnd_min = Math.floor(val_min / Math.pow(10.0, rnd_exp)) * Math.pow(10, rnd_exp);
		double rnd_max = Math.ceil(val_max / Math.pow(10.0, rnd_exp)) * Math.pow(10, rnd_exp);

		return new Point2D.Double(rnd_min, rnd_max);
	}
	
	private Rectangle2D.Double computeBounds(List<Point2D> points)
	{
		Point2D first = points.get(0);
		
		double minX = first.getX();
		double minY = first.getY();
		double maxX = first.getX();
		double maxY = first.getY();
		
		for (Point2D pt : points)
		{
			if (pt.getX() < minX)
				minX = pt.getX();

			if (pt.getY() < minY)
				minY = pt.getY();

			if (pt.getX() > maxX)
				maxX = pt.getX();

			if (pt.getY() > maxY)
				maxY = pt.getY();
		}

		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}


	private static double findMultiplier(double size, double range, double desiredSpacing)
	{
		double count = size / desiredSpacing;

		double ratio = range / count;

		double[] list = { 500.0, 200.0, 100.0, 50.0, 20.0, 10.0, 5.0, 2.0, 1.0, 0.5, 0.25, 0.1 };

		for (int i = 0; i < list.length; i++)
		{
			if (ratio > list[i])		// * 0.5
			{
				double rem = ratio % list[i];
				return ratio - rem; 
			}
		}

		return ratio;
	}
	
	private void drawSeries(List<Point2D> series, Graphics2D g)
	{
		Point2D first = series.get(0);
		Path2D path = new Path2D.Double();

		double x = ValueXToScreenX(first.getX());
		double y = ValueYToScreenY(first.getY());

		path.moveTo(x, y);
		
		for (Point2D pt : series)
		{
			x = ValueXToScreenX(pt.getX());
			y = ValueYToScreenY(pt.getY());
			
			path.lineTo(x, y);
		}
		
		Path2D filled = new Path2D.Double(path);
		
		path.lineTo(chartRect.getMaxX(), chartRect.getMaxY());
		path.lineTo(chartRect.getMinX(), chartRect.getMaxY());
		
		Color[] colors = new Color[] { new Color(128, 255, 212), new Color(123, 104, 238) };
		float[] fractions = { 0, 1 };
		float top = (float) chartRect.getMinY();
		float bottom = (float) chartRect.getMaxY();
		g.setPaint(new LinearGradientPaint(0, top, 0, bottom, fractions, colors));
		
		g.fill(path);

		g.setPaint(new Color(0, 0, 255));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.draw(filled);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
	}
	
	private double ValueYToScreenY(double val)
	{
		double fy = chartRect.getHeight() / (boundHigh.getY() - boundLow.getY());
		return chartRect.getMaxY() - fy * (val - boundLow.getY());
	}
		
	private double ValueXToScreenX(double val)
	{
		double fx = chartRect.getWidth() / (boundHigh.getX() - boundLow.getX());
		return chartRect.getMinX() + fx * (val - boundLow.getX());
	}
	
//	private double ScreenXToValueX(double val)
//	{
//		double fx = (boundHigh.getX() - boundLow.getX()) / chartRect.getWidth();
//		return boundLow.getX() + fx * (val - chartRect.getMinX());
//	}
	
	/**
	 * @param text the description on the vertical axis
	 */
	public void setVertDesc(String text)
	{
		vertDesc = text;
	}

	/**
	 * @param text the description on the horizontal axis
	 */
	public void setHorzDesc(String text)
	{
		horzDesc = text;
	}
	
}
