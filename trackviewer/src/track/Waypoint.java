
package track;

import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * A waypoint
 * @author Martin Steiger
 */
public class Waypoint extends TrackPoint
{
	private String name;
	private String description;

	/**
	 * @param pos the position
	 */
	public Waypoint(GeoPosition pos)
	{
		super(pos, null);
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	

}
