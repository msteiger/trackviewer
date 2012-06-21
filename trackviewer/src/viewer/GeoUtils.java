
package viewer;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import track.TrackPoint;

/**
 * Some geo-related utilities
 * @author Martin Steiger
 */
public class GeoUtils
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

	/**
	 * @see GeoUtils#computeDistance(double, double, double, double)
	 * @param point1 trackpoint 1
	 * @param point2 trackpoint 2
	 * @return the distance in meters
	 */
	public static double computeDistance(TrackPoint point1, TrackPoint point2)
	{
		GeoPosition pos1 = point1.getPos();
		GeoPosition pos2 = point2.getPos();

		return computeDistance(
				pos1.getLatitude(), pos1.getLongitude(), 
				pos2.getLatitude(), pos2.getLongitude());
	}

	private static double toRadian(double val)
	{
		return (Math.PI / 180) * val;
	}

}
