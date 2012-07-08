
package webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import tcx.TcxAdapter;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.PositionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;

/**
 * Fixes all elevations in all .tcx files in a 
 * folder using the {@link ElevationFixer} class.
 * @author Martin Steiger
 */
public class Converter
{
	/**
	 * @param args (ignored)
	 */
	public static void main(String[] args)
	{
		File folderIn = new File(System.getProperty("user.home") + File.separator + "trackviewer");
		File folderOut = new File(System.getProperty("user.home") + File.separator + "trackviewer" + File.separator	+ "converted");
		
		folderOut.mkdirs();

		String[] files = folderIn.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.endsWith(".tcx");
			}
		});

		TcxAdapter tcxAdapter = null;

		try
		{
			tcxAdapter = new TcxAdapter();
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
			return;
		}

		for (String fname : files)
		{
			InputStream fis = null;
			OutputStream fos = null;

			try
			{
				String fname2 = fname.substring(0, fname.length() - 4) + "_fix.tcx";
				fis = new FileInputStream(new File(folderIn, fname));
				fos = new FileOutputStream(new File(folderOut, fname2));

				fixElevations(tcxAdapter, fis, fos);

				System.out.println("Converted " + fname);
			}
			catch (Exception e)
			{
				System.out.println("Error converting " + fname);
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if (fos != null)
						fos.close();

					if (fis != null)
						fis.close();
				}
				catch (Exception e)
				{
					// ignore
				}
			}
		}
	}

	private static void fixElevations(TcxAdapter tcx, InputStream is, OutputStream os) throws JAXBException, IOException
	{
		TrainingCenterDatabaseT data = tcx.unmarshallObject(is);
		List<GeoPosition> route = extractRoute(data);
		List<Double> ele = ElevationFixer.getElevationsComp(route);
		setElevations(data, ele);
		tcx.marshallObject(os, data);
	}

	private static void setElevations(TrainingCenterDatabaseT tcx, List<Double> ele)
	{
		Iterator<Double> it = ele.iterator();

		for (ActivityT activity : tcx.getActivities().getActivity())
		{
			for (ActivityLapT lap : activity.getLap())
			{
				for (TrackT trk : lap.getTrack())
				{
					for (TrackpointT pt : trk.getTrackpoint())
					{
						PositionT pos = pt.getPosition();

						if (pos != null)
						{
							double elevation = it.next();
							
							if (elevation < -32000)
								throw new IllegalArgumentException("Invalid elevation: " + elevation);
								
							pt.setAltitudeMeters(elevation);
						}
					}
				}
			}
		}
	}

	private static List<GeoPosition> extractRoute(TrainingCenterDatabaseT tcx)
	{
		List<GeoPosition> route = new ArrayList<GeoPosition>();

		for (ActivityT activity : tcx.getActivities().getActivity())
		{
			for (ActivityLapT lap : activity.getLap())
			{
				for (TrackT trk : lap.getTrack())
				{
					for (TrackpointT pt : trk.getTrackpoint())
					{
						PositionT pos = pt.getPosition();

						if (pos != null)
						{
							double lat = pos.getLatitudeDegrees();
							double lon = pos.getLongitudeDegrees();
							GeoPosition gp = new GeoPosition(lat, lon);

							route.add(gp);
						}
					}
				}
			}
		}

		return route;
	}

}
