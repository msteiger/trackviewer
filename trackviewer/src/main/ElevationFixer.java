
package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tcx.TcxAdapter;
import track.Track;
import track.TrackPoint;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class ElevationFixer
{
	private static final Log log = LogFactory.getLog(ElevationFixer.class);
	
	public static final String u = "http://open.mapquestapi.com/elevation/v1/profile?shapeFormat=raw&latLngCollection=";
	
	public static void main(String[] args) throws IOException, JAXBException
	{
		TcxAdapter ta = new TcxAdapter();
		fixTrack(ta.read(new FileInputStream("E:\\2011-06-04-12-44-54.tcx")).get(0));
	}
	
	public static void fixTrack(Track t)
	{
		String s = "";
		
		for (TrackPoint p : t.getPoints())
		{
			GeoPosition pos = p.getPos();

			s = s + pos.getLatitude();
			s = s + ",";
			s = s + pos.getLongitude();
			s = s + ",";
		}

        try
		{
        	System.out.println(u + s);
    		String response = queryUrl(u + s);
    		System.out.println(response);
    		
			List<Double> data = handleResponse(response);
			fixElevation(t, data);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void fixElevation(Track t, List<Double> elevations)
	{
		List<TrackPoint> points = t.getPoints();

		if (elevations.size() != points.size())
		{
			String msg = "Number of datapoints (" + elevations.size() + ") not equal to number of track points ("+ points.size()+ ")";
			log.error(msg);
			return;
//			throw new IllegalArgumentException(msg);
		}
		
		for (int i = 0; i < points.size(); i++) 
		{
			points.get(i).setElevation(elevations.get(i));
		}
		
		log.info("Updated " + points.size() + " elevations");
	}

	private static String queryUrl(String string) throws IOException
	{
		System.out.println(string.length());
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
