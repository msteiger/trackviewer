package common;

import main.GeoPos;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * Some geo-related utilities
 *
 * @author Martin Steiger
 */
public class GeoUtils {

    /**
     * WGS84 The flattening factor 1/f of the earth spheroid
     */
    public static final double WGS84_EARTH_FLATTENING = 1.0 / 298.257223563;

    /**
     * WGS84 The (transverse) major (equatorial) radius
     */
    public static final double WGS84_EARTH_MAJOR = 6378137.0;

    /**
     * WGS84 The polar semi-minor (conjugate) radius
     */
    public static final double WGS84_EARTH_MINOR = 
            WGS84_EARTH_MAJOR * (1.0 - WGS84_EARTH_FLATTENING);

    /**
     * The mean radius as defined by the International Union of Geodesy and
     * Geophysics (IUGG)
     */
    public static final double WGS84_MEAN_RADIUS = 
            (2 * WGS84_EARTH_MAJOR + WGS84_EARTH_MINOR) / 3.0;

    /**
     * This uses the "haversine" formula to calculate the great-circle distance
     * between two points � that is, the shortest distance over the earth's
     * surface � giving an 'as-the-crow-flies' distance between the points
     *
     * @param lat1 latitude of point 1
     * @param lon1 longitude of point 1
     * @param lat2 latitude of point 2
     * @param lon2 longitude of point 2
     * @return distance in meters
     */
    public static double computeDistance(double lat1, double lon1, double lat2, double lon2) {
        double radius = 6371000; // 6371 kilometers == 3960 miles

        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        // a is the square of half the chord length between the points
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(deltaLon / 2)
                * Math.sin(deltaLon / 2);

        // c is the angular distance in radians
        double c = 2 * Math.asin(Math.min(1, Math.sqrt(a)));

        return radius * c;
    }

    /**
     * @see GeoUtils#computeDistance(double, double, double, double)
     * @param pos1 position 1
     * @param pos2 position 2
     * @return the distance in meters
     */
    public static double computeDistance(GeoPosition pos1, GeoPosition pos2) {
        return computeDistance(
                pos1.getLatitude(), pos1.getLongitude(),
                pos2.getLatitude(), pos2.getLongitude());
    }

    /**
     * Get a new {@link GeoPos} distanceMeters away on the compass bearing
     * azimuthDegrees from the {@link GeoPos} point - accurate to better than
     * 200m in 140km (20m in 14km) in the UK
     *
     * @param point the point
     * @param distanceMeters the distance in meters
     * @param azimuthDegrees the azimuth in degrees
     * @return the new point
     */
    public static GeoPos pointAtRangeAndBearing(GeoPos point, double distanceMeters, double azimuthDegrees) {
        double latr = point.lat() * Math.PI / 180.0;
        double lonr = point.lng() * Math.PI / 180.0;

        double coslat = Math.cos(latr);
        double sinlat = Math.sin(latr);
        double az = azimuthDegrees * Math.PI / 180.0;
        double cosaz = Math.cos(az);
        double sinaz = Math.sin(az);
        double dr = distanceMeters / WGS84_MEAN_RADIUS; // distance in radians
        double sind = Math.sin(dr);
        double cosd = Math.cos(dr);

        double lat = Math.asin((sinlat * cosd) + (coslat * sind * cosaz))
                * 180.0 / Math.PI;
        double lon = Math.atan2((sind * sinaz), (coslat * cosd) - (sinlat * sind * cosaz))
                + lonr * 180.0 / Math.PI;

        return new GeoPos(lat, lon);
    }

}
