
package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.painter.AbstractPainter;

/**
 * Paints colored markers along the track
 * @author Martin Steiger
 */
public class MarkerPainter extends AbstractPainter<JXMapViewer>
{
	private List<GeoPosition> track;
	private List<Integer> markers = new ArrayList<Integer>();
	private Color color;
	
	/**
	 * @param track the track
	 */
	public MarkerPainter(List<GeoPosition> track, Color color)
	{
		this.track = track;
		this.color = color;
	}

	public void clearMarkers()
	{
		markers.clear();
	}
	
	public void addMarker(int index)
	{
		if (index < 0 || index > track.size())
			throw new IllegalArgumentException("Pos " + index + " not in track");

		markers.add(index);
		
		setDirty(true);
	}
	
	@Override
	public void doPaint(Graphics2D g, JXMapViewer map, int unused1, int unused2)
	{
		// incorporate zoom to some extent
		int width = Math.max(1, 10 - map.getZoom() * 2);
		
		// do the drawing
		g.setColor(new Color(128, 0, 0));
		g.setStroke(new BasicStroke(width + 2));

		draw(g, map, 5 * width); 
		
		// do the drawing again
		g.setColor(color);
		g.setStroke(new BasicStroke(width));
		
		draw(g, map, 5 * width);
	}
	
	private void draw(Graphics2D g, JXMapViewer map, double len)
	{
		for (Integer idx : markers)
		{
			GeoPosition gp = track.get(idx);
			Point2D p = map.convertGeoPositionToPoint(gp);
			Point2D dir = getDirection(idx, map);

			if (dir != null)
			{
				Point2D n = new Point2D.Double(dir.getY(), -dir.getX());
	
				g.drawLine(
						(int)(p.getX() - n.getX() * len), (int)(p.getY() - n.getY() * len), 
						(int)(p.getX() + n.getX() * len), (int)(p.getY() + n.getY() * len));
			}
		}
	}

	private Point2D getDirection(int index, JXMapViewer map)
	{
		int range = 1;
		double distSq = 0;

		double dx = 0;
		double dy = 0;
		
		while (distSq < 50 && range < 20)
		{
			// compute direction from [-ran^ge..range] around index
			int lowBound = Math.max(index - range, 0);
			int highBound = Math.min(index + range, track.size() - 1);

			range++;

			GeoPosition gpHigh = track.get(highBound);
			GeoPosition gpLow = track.get(lowBound);
			
			Point2D ptHigh = map.convertGeoPositionToPoint(gpHigh);
			Point2D ptLow = map.convertGeoPositionToPoint(gpLow);
			
			dx = ptHigh.getX() - ptLow.getX(); 
			dy = ptHigh.getY() - ptLow.getY();
			
			distSq = dx * dx + dy * dy;

			if (lowBound == 0 && highBound == track.size() - 1)
				break;		// this is as good as it gets
		}

		if (Math.abs(distSq) < 0.01)
			return null;

		double dist = Math.sqrt(distSq);
		
		return new Point2D.Double(dx / dist, dy/ dist);
	}

	/**
	 * Return the list of route positions as unmodifiable list
	 * @return the route
	 */
	public List<GeoPosition> getRoute()
	{
		return Collections.unmodifiableList(track);
	}
}
