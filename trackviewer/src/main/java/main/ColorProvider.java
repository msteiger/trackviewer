
package main;

import java.awt.Color;

/**
 * Provides uniform colors
 * @author Martin Steiger
 */
public class ColorProvider
{
	/**
	 * Creates a "main" color
	 * @param i the identifier
	 * @return the created color
	 */
	public static Color getMainColor(int i)
	{
		float hue = ((i*3) % 5) * 0.2f;
		float sat = 0.7f;
		float bright = 0.5f;
		
		return new Color(Color.HSBtoRGB(hue, sat, bright));
	}
	
	/**
	 * Creates a "top" color
	 * @param i the identifier
	 * @return the created color
	 */
	public static Color getTopColor(int i)
	{
		float hue = ((i*3+1) % 5) * 0.2f;
		float sat = 0.5f;
		float bright = 1.0f;
		
		return new Color(Color.HSBtoRGB(hue, sat, bright));
	}
	
	/**
	 * Creates a "bottom" color
	 * @param i the identifier
	 * @return the created color
	 */
	public static Color getBottomColor(int i)
	{
		float hue = (i*3 % 5) * 0.2f;
		float sat = 0.7f;
		float bright = 0.9f;
		
		return new Color(Color.HSBtoRGB(hue, sat, bright));
	}
}
