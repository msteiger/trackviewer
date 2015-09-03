
package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.LocalResponseCache;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;

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
	
	private List<RoutePainter> routePainters = new ArrayList<RoutePainter>();
	private List<MarkerPainter> markerPainters = new ArrayList<MarkerPainter>();
	
	/**
	 * Constructs a new instance
	 */
	public MapViewer()
	{
		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		tileFactory.setThreadPoolSize(8);
		mapViewer.setTileFactory(tileFactory);

		// Setup local file cache
		String baseURL = info.getBaseURL();
		File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
		LocalResponseCache.installResponseCache(baseURL, cacheDir, false);

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

		markerPainters.clear();
		routePainters.clear();
		
		List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
		
		int i = 0;
		for (Track track : tracks)
		{
			List<GeoPosition> route = track.getRoute();
			Color color = ColorProvider.getMainColor(i++);

			MarkerPainter markerPainter = new MarkerPainter(route, color);  
			RoutePainter routePainter = new RoutePainter(route, color);
			
			markerPainters.add(markerPainter);
			routePainters.add(routePainter);
			
			markerPainter.addMarker(0);
			markerPainter.addMarker(route.size() - 1);

			painters.add(routePainter);
			painters.add(markerPainter);
		}
		
		painter.setPainters(painters);

	}

	/**
	 * @param track the track index
	 * @param index the index of the track point
	 */
	public void setMarker(int track, int index)
	{
		MarkerPainter mp = markerPainters.get(track);

		int minIdx = 0;
		int maxIdx = mp.getRoute().size() - 1;

		mp.clearMarkers();
		mp.addMarker(minIdx);
		mp.addMarker(maxIdx);
		
		if (index > minIdx && index < maxIdx)
		{
			mp.addMarker(index);
		}
	}
}
