
package webservice;

import java.util.ArrayList;
import java.util.List;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * Uses polyline compression as described by google developers:
 * "Encoded Polyline Algorithm Format"
 * https://developers.google.com/maps/documentation/utilities/polylinealgorithm
 * and MapQuest
 * "Compressed Lat/Lng Encoding/Decoding"
 * http://platform.beta.mapquest.com/common/encodedecode.html
 * @author Martin Steiger
 */
public class PolylineEncoder
{
	/**
	 * @param points the list of points with latitude/longitude
	 * @param prec the precision in digits (e.g. 5 or 6)
	 * @return the compressed data
	 */
	public static String compress(List<GeoPosition> points, int prec)
	{
		int oldLat = 0, oldLng = 0;
		String encoded = "";
		double precision = Math.pow(10, prec);

		for (GeoPosition pos : points)
		{
			//  Round to N decimal places
			int lat = (int) Math.round(pos.getLatitude() * precision);
			int lng = (int) Math.round(pos.getLongitude() * precision);

			//  Encode the differences between the points
			encoded += encodeNumber(lat - oldLat);
			encoded += encodeNumber(lng - oldLng);

			oldLat = lat;
			oldLng = lng;
		}
		return encoded;
	}

	/** 
	 * @param encoded the compressed data
	 * @param prec the precision in digits (e.g. 5 or 6)
	 * @return points the list of points with latitude/longitude
	 */
	public static List<GeoPosition> decompress (String encoded, int prec) 
	{
		double precision = Math.pow(10, -prec);
		int len = encoded.length(), index = 0, lat = 0, lng = 0;

		List<GeoPosition> array = new ArrayList<GeoPosition>();
		while (index < len)
		{
			int b, shift = 0, result = 0;
			do
			{
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			}
			while (b >= 0x20);
			int dlat = (((result & 1) > 0) ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do
			{
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			}
			while (b >= 0x20);
			int dlng = (((result & 1) > 0) ? ~(result >> 1) : (result >> 1));
			lng += dlng;
			array.add(new GeoPosition(lat * precision, lng * precision));
		}
		
		return array;
	}

	private static String encodeNumber(int num)
	{
		num = num << 1;
		if (num < 0)
		{
			num = ~(num);
		}
		String encoded = "";
		while (num >= 0x20)
		{
			encoded += Character.toString((char) ((0x20 | (num & 0x1f)) + 63));
			num >>= 5;
		}
		encoded += Character.toString((char) (num + 63));
		return encoded;
	}
}
