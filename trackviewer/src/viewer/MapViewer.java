
package viewer;

import java.io.File;

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

import sample2_waypoints.RoutePainter;
import track.Track;

/**
 * TODO Type description
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
		
		this.setLayout(new FillLayout());
		add(mapViewer);
	}
	
	/**
	 * Displays a track route
	 * @param track the track
	 */
	public void setRoute(Track track)
	{
		// Set the focus
		mapViewer.setZoom(10);
		mapViewer.setAddressLocation(track.getPoints().iterator().next().getPos());

		// Create route from geo-positions
		RoutePainter routePainter = new RoutePainter(track.getRoute());
		
		painter.addPainter(routePainter);

	}
}
