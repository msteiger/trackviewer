
package viewer;

import java.util.List;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import track.Track;
import track.TrackPoint;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class TrackComputer
{
	/**
	 * This uses the "haversine" formula to calculate the great-circle 
	 * distance between two points – that is, the shortest distance 
	 * over the earth's surface – giving an 'as-the-crow-flies' distance between the points
	 * @param lat1 latitude of point 1
	 * @param lon1 longitude of point 1
	 * @param lat2 latitude of point 2
	 * @param lon2 longitude of point 2
	 * @return distance in meters
	 */
	public static double computeDistance(double lat1, double lon1, double lat2, double lon2)
	{
		double radius = 6371000; // 6371 kilometers == 3960 miles

		double deltaLat = toRadian(lat2 - lat1);
		double deltaLon = toRadian(lon2 - lon1);

		// a is the square of half the chord length between the points
		double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(toRadian(lat1))
				* Math.cos(toRadian(lat2)) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

		// c is the angular distance in radians
		double c = 2 * Math.asin(Math.min(1, Math.sqrt(a)));

		return radius * c;
	}

	public static double computeDistance(TrackPoint point1, TrackPoint point2)
	{
		GeoPosition pos1 = point1.getPos();
		GeoPosition pos2 = point2.getPos();

		return computeDistance(
				pos1.getLatitude(), pos1.getLongitude(), 
				pos2.getLatitude(), pos2.getLongitude());
	}

	public static void repairTrackData(Track track)
	{
		fixNonNullStarts(track);
		fixDistances(track);
		
		for (int i=0; i<track.getPoints().size(); i++)
		{
			computeSpeed(track, i);
		}
	}

	private static void fixDistances(Track track)
	{
		List<TrackPoint> points = track.getPoints();

		if (points.isEmpty())
			return;

		TrackPoint prevPoint = points.get(0);

		for (TrackPoint point : points)
		{
			if (point.getDistance() <= prevPoint.getDistance())
			{
				double dist = prevPoint.getDistance() + computeDistance(prevPoint, point);
				point.setDistance(dist);
			}

			prevPoint = point;
		}
	}

	private static void fixNonNullStarts(Track track)
	{
		List<TrackPoint> points = track.getPoints();

		if (points.isEmpty())
			return;

		double offset = points.get(0).getDistance();

		if (Math.abs(offset) < 0.01)
			return;

		for (TrackPoint point : points)
		{
			point.setDistance(point.getDistance() - offset);
		}

	}

	private static void computeSpeed(Track track, int index)
	{
		final int range = 2;

		List<TrackPoint> points = track.getPoints();

		// compute speed from [-range..range] around index
		int lowBound = Math.max(index - range, 0);
		int highBound = Math.min(index + range, points.size() - 1);

		TrackPoint high = points.get(highBound);
		TrackPoint low = points.get(lowBound);

		double deltaDistance = high.getDistance() - low.getDistance();
		long deltaTime = high.getTime().getTime() - low.getTime().getTime();

		if (deltaTime != 0)
		{
			points.get(index).setSpeed(deltaDistance / 3600000.0);
		}
	}

	private static double toRadian(double val)
	{
		return (Math.PI / 180) * val;
	}
}
