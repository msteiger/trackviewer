
package gpx;

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

import com.topografix.gpx._1._1.GpxType;
import com.topografix.gpx._1._1.TrkType;
import com.topografix.gpx._1._1.TrksegType;
import com.topografix.gpx._1._1.WptType;

/**
 * Reads track data from .gpx files
 * @author Martin Steiger
 */
public class GpxReader
{
	/**
	 * @param is the input stream
	 * @return the track data
	 * @throws IOException if the data cannot be read
	 */
	public static List<Track> read(InputStream is) throws IOException
	{
		GpxType gpx;
		try
		{
			gpx = unmarshallObject(is);
		}
		catch (JAXBException e)
		{
			throw new IOException("Error parsing inputstream", e);
		}

		ArrayList<Track> list = new ArrayList<Track>();
		
		for (TrkType trk : gpx.getTrk())
		{
			for (TrksegType seg : trk.getTrkseg())
			{
				Track track = new Track();

				for (WptType pt : seg.getTrkpt())
				{
					double lat = pt.getLat().doubleValue();
					double lon = pt.getLon().doubleValue();
					double ele = pt.getEle().doubleValue();
					GregorianCalendar cal = pt.getTime().toGregorianCalendar();
					GeoPosition pos = new GeoPosition(lat, lon);

					TrackPoint tp = new TrackPoint(pos, cal.getTime());
					tp.setElevation(ele);
					track.addPoint(tp);
				}

				list.add(track);
			}
		}

		return list;
	}
	
	private static GpxType unmarshallObject(InputStream is) throws JAXBException
	{
		String packageName = GpxType.class.getPackage().getName();
		JAXBContext context = JAXBContext.newInstance(packageName);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		JAXBElement<GpxType> jaxbObject = (JAXBElement<GpxType>) unmarshaller.unmarshal(is);
		return jaxbObject.getValue();
	
	}
}
