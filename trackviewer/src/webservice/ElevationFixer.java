
package webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Retrieves a list of elevations from a web service
 * @author Martin Steiger
 */
public class ElevationFixer
{
	private static final Log log = LogFactory.getLog(ElevationFixer.class);
	
	private static final String url = "http://open.mapquestapi.com/elevation/v1/profile?useFilter=true";
	
	/**
	 * Retrieves a list of elevations from a web service with plain lat/lon kvp. 
	 * @param route the route
	 * @return the list of elevations
	 * @throws IOException if something goes wrong
	 */
	public static List<Double> getElevationsRaw(List<GeoPosition> route) throws IOException
	{
		String s = "&shapeFormat=raw&latLngCollection=";
		
		// TODO: add splitting up in chunks here 
		// or use the code from the compressed version in both versions
		
		for (GeoPosition pos : route)
		{
			s = s + pos.getLatitude();
			s = s + ",";
			s = s + pos.getLongitude();
			s = s + ",";
		}
		
		return queryElevations(s);
	}
	
	/**
	 * Retrieves a list of elevations from a web service using 
	 * google polyline compression URLs.
	 * @param routeFull the route
	 * @return the list of elevations
	 * @throws IOException if something goes wrong
	 */
	public static List<Double> getElevationsComp(List<GeoPosition> routeFull) throws IOException
	{
		final String s = "&shapeFormat=cmp&latLngCollection=";
		
		final int chunkSize = 1500;	// this is just a guess that seems to work for MapQuest Elevation API v1
		
		int min = 0;
		int max = Math.min(routeFull.size(), min + chunkSize);
		
		List<Double> ele = new ArrayList<Double>();
		
		while (min < max)
		{
			List<GeoPosition> route = routeFull.subList(min, max);

			String compressed = PolylineEncoder.compress(route, 5);
		
			List<Double> result = queryElevations(s + compressed);
			
			if (result.size() != max - min)
			{
				throw new IllegalStateException("Elevation query returned only " + result.size() + " instead of " + (max - min) + " points");
			}
			
			ele.addAll(result);

			min = max;
			max = Math.min(routeFull.size(), min + chunkSize);
		}
		
		return ele;
	}
	
	
	private static List<Double> queryElevations(String s) throws IOException
	{
        try
		{
    		String response = queryUrl(url + s);
    		
    		handleInfo(response);
    		
			List<Double> data = handleResponse(response);
			return data;
		}
		catch (JSONException e)
		{
			throw new IOException(e);
		}
	}
	
	private static String queryUrl(String string) throws IOException
	{
		InputStream is = null;
		try
		{
			URL url = new URL(string);

			URLConnection conn = url.openConnection();
			is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader in = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String line;

			while ((line = in.readLine()) != null)
			{
				sb.append(line);
			}

			return sb.toString();
		}
		finally
		{
			if (is != null)
				is.close();
		}
	}
	
	private static void handleInfo(String source) throws JSONException
	{
		JSONObject obj = new JSONObject(source);

		JSONObject info = obj.getJSONObject("info");
		
		int code = info.getInt("statuscode");
		if (code != 0)
		{
			log.info(code);
		}
		
		JSONArray msgs = (JSONArray)info.get("messages");
		for (int i = 0; i < msgs.length(); i++)
		{
			log.info(msgs.getString(i));
		}

	}

	private static List<Double> handleResponse(String source) throws JSONException
	{
		JSONObject obj = new JSONObject(source);
		
		JSONArray arr = obj.getJSONArray("elevationProfile");

		List<Double> data = new ArrayList<Double>();
		
		for (int i = 0; i < arr.length(); i++)
		{
			JSONObject obj2 = (JSONObject)arr.get(i);

			double val = obj2.getDouble("height");
			data.add(Double.valueOf(val));		// cache often-used values
		}
		
		return data;
	}
}
