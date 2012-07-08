
package tcx;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import track.Track;
import track.TrackPoint;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityLapT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ActivityT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.ObjectFactory;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.PositionT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrackpointT;
import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;

/**
 * Reads track data from .gpx files
 * @author Martin Steiger
 */
public class TcxAdapter
{
	private final JAXBContext context;

	/**
	 * @throws JAXBException occurs if .. 
	 * <ol>
     *   <li>failure to locate either ObjectFactory.class or jaxb.index in the packages</li>
     *   <li>an ambiguity among global elements contained in the contextPath</li>
     *   <li>failure to locate a value for the context factory provider property</li>
     *   <li>mixing schema derived packages from different providers on the same contextPath</li>
     * </ol>
	 */
	public TcxAdapter() throws JAXBException
	{
		Class<?> clazz = TrainingCenterDatabaseT.class;
		String packageName = clazz.getPackage().getName();
		context = JAXBContext.newInstance(packageName);
	}

	public List<Track> convertToTracks(TrainingCenterDatabaseT tcx)
	{
		ArrayList<Track> list = new ArrayList<Track>();

		for (ActivityT activity : tcx.getActivities().getActivity())
		{
			for (ActivityLapT lap : activity.getLap())
			{
				Track track = new Track();

				for (TrackT trk : lap.getTrack())
				{
					
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

				}
				list.add(track);
			}
		}

		return list;
	}

	/**
	 * @param is the input stream
	 * @return the track data
	 * @throws JAXBException if the data cannot be read
	 */
	public TrainingCenterDatabaseT unmarshallObject(InputStream is) throws JAXBException
	{
		Unmarshaller unmarshaller = context.createUnmarshaller();

		JAXBElement<TrainingCenterDatabaseT> jaxbObject;
		jaxbObject = (JAXBElement<TrainingCenterDatabaseT>) unmarshaller.unmarshal(is);

		return jaxbObject.getValue();
	}
	
	/**
	 * @param os the output stream
	 * @param value the value to be written
	 * @throws JAXBException if the data cannot be written
	 */
	public void marshallObject(OutputStream os, TrainingCenterDatabaseT value) throws JAXBException
	{
		ObjectFactory of = new ObjectFactory();

		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.marshal(of.createTrainingCenterDatabase(value), os);
	}	
	
}
