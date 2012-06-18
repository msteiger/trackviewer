package viewer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import tcx.TcxReader;
import track.Track;

/**
 * A simple sample application that shows
 * a OSM map of Europe
 * @author Martin Steiger
 */
public class MainFrame extends JFrame
{
	private static final long serialVersionUID = -9215006987029836062L;

	private MapViewer mapViewer = new MapViewer();

	/**
	 * Constructs a new instance
	 */
	public MainFrame()
	{
		super("TrackViewer");

		// Read in route
		List<Track> tracks;
		try
		{
			tracks = TcxReader.read(new FileInputStream("E:\\2012-06-10-12-25-41.tcx"));
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, e);
			return;
		}


		mapViewer.setRoute(tracks.get(0));
		
		add(mapViewer);
	}
	
	
	/**
	 * @param args the program args (ignored)
	 */
	public static void main(String[] args)
	{
		JFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
