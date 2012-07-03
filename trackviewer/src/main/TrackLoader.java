
package main;

import gpx.GpxAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import tcx.TcxAdapter;
import track.Track;

/**
 * Loads a series of track files from a folder
 * in an asynchronous manner.
 * @author Martin Steiger
 */
public class TrackLoader
{
	public static void readTracks(final File folder, final TrackLoadListener cb)
	{
		Thread th = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				fill(folder, cb);
			}
		});
		
		th.start();
	}

	private static void fill(File folder, TrackLoadListener cb)
	{
		String[] files = folder.list(new FilenameFilter()
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
			JOptionPane.showMessageDialog(null, e);
//			log.error("Error initializing TcxAdapter", e);
			return;
		}

		for (String fname : files)
		{
			FileInputStream fis = null;

			try
			{
				fis = new FileInputStream(new File(folder, fname));
				List<Track> read = tcxAdapter.read(fis);
				
				for (Track t : read)
				{
					// skip empty tracks
					if (!t.getPoints().isEmpty())
					{
						TrackComputer.repairTrackData(t);
						cb.trackLoaded(t);
					}
				}
				
				System.out.println("Loaded " + fname);
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, e);
			}
			finally
			{
				try
				{
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

	public static void save(String fname, Track track) throws IOException
	{
		OutputStream os = null;
		
		try
		{
			os = new FileOutputStream(fname);
			GpxAdapter gpxAdapter = new GpxAdapter();
			gpxAdapter.write(os, Collections.singletonList(track));
		}
		catch (JAXBException e)
		{
			throw new IOException(e);
		}
		finally
		{
			if (os != null)
				os.close();
		}
	}


}
