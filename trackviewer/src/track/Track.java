
package track;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * Represents a track
 * @author Martin Steiger
 */
public class Track
{
	private final List<TrackPoint> points = new ArrayList<TrackPoint>();
	private final List<Waypoint> waypoints = new ArrayList<Waypoint>();
			
	private List<GeoPosition> route = new AbstractList<GeoPosition>()
	{
		@Override
		public GeoPosition get(int index)
		{
			return points.get(index).getPos();
		}

		@Override
		public int size()
		{
			return points.size();
		}

	};

	private String name;
	private Date startTime;
	private Double altDiff;

	/**
	 * Default constructor (no name set)
	 */
	public Track()
	{
	}

	/**
	 * @return an unmodifiable list of geo-positions
	 */
	public List<GeoPosition> getRoute()
	{
		return route;		// read-only anyway
	}

	/**
	 * @return an unmodifiable list of track points
	 */
	public List<TrackPoint> getPoints()
	{
		return Collections.unmodifiableList(points);
	}

	/**
	 * @param point the track point
	 */
	public void addPoint(TrackPoint point)
	{
		points.add(point);
	}
	
	/**
	 * @param point the waypoint
	 */
	public void addWaypoint(Waypoint point)
	{
		waypoints.add(point);
	}
	/**
	 * @return an unmodifiable list of waypoints
	 */
	public List<Waypoint> getWaypoints()
	{
		return Collections.unmodifiableList(waypoints);
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the average speed in km/h
	 */
	public double getAverageSpeed()
	{
		double sum = 0;
		for (TrackPoint point : points)
		{
			sum += point.getSpeed();
		}
		
		return sum / points.size();
	}
	
	/**
	 * @param startTime the start time the track
	 */
	public void setStartTime(Date startTime)
	{
		this.startTime = startTime; 
	}
	
	/**
	 * @return the start time the track
	 */
	public Date getStartTime()
	{
		return startTime; 
	}
	
	public double getTotalElevationDifference()
	{
		if (points.isEmpty())
			return 0;

		if (altDiff == null)
		{
			double total = 0;
			
			double prevEle = points.get(0).getElevation(); 
			
			for (TrackPoint pt : points)
			{
				double ele = pt.getElevation();
				double delta = ele - prevEle;
				
				total += Math.abs(delta);
				
				prevEle = ele;
			}
			
			altDiff = total * 0.5;		// 50% up, 50% down
		}
		
		return altDiff;
	}
	
	/**
	 * @return the total distance of the track in meters
	 */
	public double getTotalDistance()
	{
		if (points.isEmpty())
			return 0;
		
		return points.get(points.size() - 1).getDistance();
	}

	/**
	 * @return the total time of the track
	 */
	public Date getTotalTime()
	{
		return points.get(points.size() - 1).getTime();
	}
}
