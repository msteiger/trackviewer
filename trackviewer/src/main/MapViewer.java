
package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.OSMTileFactoryInfo;
import org.jdesktop.swingx.input.PanMouseInputListener;
import org.jdesktop.swingx.input.ZoomMouseWheelListenerCursor;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.LocalResponseCache;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.painter.CompoundPainter;

import track.Track;

/**
 * A wrapper for the actual {@link JXMapViewer} component.
 * It connects to the typical application classes.
 * @author Martin Steiger
 */
public class MapViewer extends JComponent
{
	private static final long serialVersionUID = -1636285199192286728L;

	private CompoundPainter<JXMapViewer> painter;

	private JXMapViewer mapViewer = new JXMapViewer();

	/**
	 * Constructs a new instance
	 */
	public MapViewer()
	{
		// Setup local file cache
		File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
		LocalResponseCache.installResponseCache(cacheDir, false);

		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		tileFactory.setThreadPoolSize(8);
		mapViewer.setTileFactory(tileFactory);
	
		// Add interactions
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
		
		painter = new CompoundPainter<JXMapViewer>();
		mapViewer.setOverlayPainter(painter);
		
		GeoPosition frankfurt = new GeoPosition(50,  7, 0, 8, 41, 0);

		// Set the focus
		mapViewer.setZoom(10);
		mapViewer.setAddressLocation(frankfurt);
		
		setLayout(new BorderLayout());
		add(mapViewer, BorderLayout.CENTER);
	}
	
	/**
	 * Displays one or more track routes
	 * @param tracks the list of track
	 */
	public void showRoute(List<Track> tracks)
	{
		// Set the focus
//		mapViewer.setZoom(10);
//		mapViewer.setAddressLocation(track.getPoints().iterator().next().getPos());

		List<RoutePainter> painters = new ArrayList<RoutePainter>();
		
		int i = 0;
		for (Track track : tracks)
		{
			painters.add(new RoutePainter(track.getRoute(), getRouteColor(i++)));
		}
		
		painter.setPainters(painters);

	}

	private Color getRouteColor(int i)
	{
		Color colors[] = { Color.RED, Color.BLUE, Color.LIGHT_GRAY, Color.YELLOW, Color.CYAN };
		return colors[i % colors.length];
	}
}
