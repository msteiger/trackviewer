
package main;

import java.util.List;

import common.GeoUtils;

/**
 * Code to find the distance in metres between a lat/lng point and a polyline of
 * lat/lng points All in WGS84. Free for any use.
 * @author Bill Chadwick (2007)
 */
public class GeoPos
{
	private double x, y, z;
	private double lat, lon;

	/** 
	 * Construct a GeoPos from its latitude and longitude in degrees
	 * @param lat latitude in degrees
	 * @param lon longitude in degrees
	 */
	public GeoPos(double lat, double lon)
	{
		double theta = (lon * Math.PI / 180.0);
		double rlat = geocentricLatitude(lat * Math.PI / 180.0);
		double c = Math.cos(rlat);
		
		this.lat = lat;
		this.lon = lon;

		this.x = c * Math.cos(theta);
		this.y = c * Math.sin(theta);
		this.z = Math.sin(rlat);
	}

	/**
	 * @return the latitude in degrees
	 */
	public double lat()
	{
		return lat;
	}

	/**
	 * @return the longitude in degrees
	 */
	public double lng()
	{
		return lon;
	}

	/**
	 * Convert from geographic to geocentric latitude (radians).
	 * @param geographicLatitude the geographic latitude
	 * @return the geocentric latitude in radians
	 */
	private double geocentricLatitude(double geographicLatitude)
	{
		double flattening = GeoUtils.WGS84_EARTH_FLATTENING;
		double f = (1.0 - flattening) * (1.0 - flattening);
		return Math.atan((Math.tan(geographicLatitude) * f));
	}

//	/**
//	 * Convert from geocentric to geographic latitude (radians)
//	 * @param geocentricLatitude the geocentric Latitude
//	 * @return the geographic latitude in radians
//	 */
//	private static double geographicLatitude(double geocentricLatitude)
//	{
//		double flattening = 1.0 / earthFlattening;
//		double f = (1.0 - flattening) * (1.0 - flattening);
//		return Math.atan(Math.tan(geocentricLatitude) / f);
//	}

	/**
	 * Returns the two antipodal points of intersection of two great circles
	 * defined by the arcs geo1 to geo2 and geo3 to geo4. Returns a point as a
	 * {@link GeoPos}, use .antipode to get the other point
	 * @param geo1 start of first arc
	 * @param geo2 end of first arc
	 * @param geo3 start of second arc
	 * @param geo4 end of second arc
	 * @return the intersection point (and its antipode)
	 */
	private static GeoPos getIntersection(GeoPos geo1, GeoPos geo2, GeoPos geo3, GeoPos geo4)
	{
		GeoPos geoCross1 = geo1.crossNormalize(geo2);
		GeoPos geoCross2 = geo3.crossNormalize(geo4);
		return geoCross1.crossNormalize(geoCross2);
	}

	private static double radiansToMeters(double rad)
	{
		return rad * GeoUtils.WGS84_MEAN_RADIUS;
	}

//	private static double metersToRadians(double m)
//	{
//		return m / earthRadius;
//	}

//	private double getLatitudeRadians()
//	{
//		return (bdccGeoGeographicLatitude(Math.atan2(this.z, Math.sqrt((this.x * this.x) + (this.y * this.y)))));
//	}
//
//	private double getLongitudeRadians()
//	{
//		return (Math.atan2(this.y, this.x));
//	}

	private double dot(GeoPos b)
	{
		return ((this.x * b.x) + (this.y * b.y) + (this.z * b.z));
	}

	private double crossLength(GeoPos b)
	{
		double x = (this.y * b.z) - (this.z * b.y);
		double y = (this.z * b.x) - (this.x * b.z);
		double z = (this.x * b.y) - (this.y * b.x);
		return Math.sqrt((x * x) + (y * y) + (z * z));
	}

	private GeoPos scale(double s)
	{
		GeoPos r = new GeoPos(0, 0);
		r.x = this.x * s;
		r.y = this.y * s;
		r.z = this.z * s;
		return r;
	}

	private GeoPos crossNormalize(GeoPos b)
	{
		double x = (this.y * b.z) - (this.z * b.y);
		double y = (this.z * b.x) - (this.x * b.z);
		double z = (this.x * b.y) - (this.y * b.x);
		double L = Math.sqrt((x * x) + (y * y) + (z * z));
		GeoPos r = new GeoPos(0, 0);
		r.x = x / L;
		r.y = y / L;
		r.z = z / L;
		return r;
	}

	/**
	 * @return Point on opposite side of the world to this point
	 */
	private GeoPos antipode()
	{
		return this.scale(-1.0);
	}

	/**
	 * Distance in radians from this point to point v2
	 * @param v2 the other point
	 * @return the distance in radians
	 */
	private double distance(GeoPos v2)
	{
		return Math.atan2(v2.crossLength(this), v2.dot(this));
	}

	/**
	 * Returns in meters the minimum of the perpendicular distance of this point
	 * from the line segment geo1-geo2 and the distance from this point to the
	 * line segment ends in geo1 and geo2
	 * @param geo1 start of the line segment
	 * @param geo2 end of the line segment
	 * @return the distance in meters
	 */
	private double distanceToLineSegMtrs(GeoPos geo1, GeoPos geo2)
	{
		//point on unit sphere above origin and normal to plane of geo1,geo2
		//could be either side of the plane
		GeoPos p2 = geo1.crossNormalize(geo2);

		// intersection of GC normal to geo1/geo2 passing through p with GC geo1/geo2
		GeoPos ip = getIntersection(geo1, geo2, this, p2);

		//need to check that ip or its antipode is between p1 and p2
		double d = geo1.distance(geo2);
		double d1p = geo1.distance(ip);
		double d2p = geo2.distance(ip);
		//window.status = d + ", " + d1p + ", " + d2p;
		if ((d >= d1p) && (d >= d2p))
			return radiansToMeters(this.distance(ip));
		else
		{
			ip = ip.antipode();
			d1p = geo1.distance(ip);
			d2p = geo2.distance(ip);
		}
		if ((d >= d1p) && (d >= d2p))
			return radiansToMeters(this.distance(ip));
		else
			return radiansToMeters(Math.min(geo1.distance(this), geo2.distance(this)));
	}

	/**
	 * Distance in meters from point to a polyline
	 * @param poly the polyline
	 * @param point the point
	 * @return the distance in meters
	 */
	public static double distanceToPolyMtrs(List<GeoPos> poly, GeoPos point)
	{
		double d = Double.MAX_VALUE;
		int i;
		GeoPos p = new GeoPos(point.lat(), point.lng());
		for (i = 0; i < (poly.size() - 1); i++)
		{
			GeoPos p1 = poly.get(i);
			GeoPos l1 = new GeoPos(p1.lat(), p1.lng());
			GeoPos p2 = poly.get(i + 1);
			GeoPos l2 = new GeoPos(p2.lat(), p2.lng());
			double dp = p.distanceToLineSegMtrs(l1, l2);
			if (dp < d)
				d = dp;
		}
		return d;
	}
}
