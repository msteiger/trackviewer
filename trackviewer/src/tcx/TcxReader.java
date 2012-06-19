
package tcx;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import track.Track;
import track.TrackPoint;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.PositionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;

/**
 * Reads track data from .gpx files
 * @author Martin Steiger
 */
public class TcxReader
{
	/**
	 * @param is the input stream
	 * @return the track data
	 * @throws IOException if the data cannot be read
	 */
	public static List<Track> read(InputStream is) throws IOException
	{
		TrainingCenterDatabaseT tcx;
		try
		{
			tcx = unmarshallObject(is);
		}
		catch (JAXBException e)
		{
			throw new IOException("Error parsing inputstream", e);
		}

		ArrayList<Track> list = new ArrayList<Track>();

		for (ActivityT activity : tcx.getActivities().getActivity())
		{
			for (ActivityLapT lap : activity.getLap())
			{
				for (TrackT trk : lap.getTrack())
				{
					Track track = new Track("Hello World");
					
					for (TrackpointT pt : trk.getTrackpoint())
					{
						PositionT pos = pt.getPosition();

						if (pos != null)
						{
							double lat = pos.getLatitudeDegrees();
							double lon = pos.getLongitudeDegrees();
							double ele = pt.getAltitudeMeters();
							GregorianCalendar time = pt.getTime().toGregorianCalendar();
							GeoPosition gp = new GeoPosition(lat, lon);
							TrackPoint tp = new TrackPoint(gp, time.getTime());
							tp.setElevation(ele);
							track.addPoint(tp);
						}
					}

					list.add(track);
				}
			}
		}

		return list;
	}

	private static TrainingCenterDatabaseT unmarshallObject(InputStream is) throws JAXBException
	{
		String packageName = TrainingCenterDatabaseT.class.getPackage().getName();
		JAXBContext context = JAXBContext.newInstance(packageName);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		JAXBElement<TrainingCenterDatabaseT> jaxbObject;
		jaxbObject = (JAXBElement<TrainingCenterDatabaseT>) unmarshaller.unmarshal(is);
		return jaxbObject.getValue();

	}
}
