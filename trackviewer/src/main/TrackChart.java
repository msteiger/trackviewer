
package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import main.chart.JChart;
import track.Track;
import track.TrackPoint;

/**
 * The chart component as well as a toolbar
 * to configure it.
 * @author Martin Steiger
 */
public class TrackChart extends JComponent
{
	private static final long serialVersionUID = 5779546384127375283L;

	private enum ChartModeVert
	{
		Height,
		Speed,
	}

	private enum ChartModeHorz
	{
		Distance,
		Time
	}

	private ChartModeVert chartModeVert = ChartModeVert.Height;
	private ChartModeHorz chartModeHorz = ChartModeHorz.Distance;
	
	private List<Track> tracks;
	private JChart chart;
	
	private List<SelectionListener> selectionListeners = new CopyOnWriteArrayList<SelectionListener>();

	/**
	 * Creates a new instance
	 */
	public TrackChart()
	{
		chart = new JChart();
		
		MouseAdapter ma = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				select(e.getX());
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				select(e.getX());
			}
			
			private void select(int x)
			{
				chart.setMarker(x);

				for (int i = 0; i < chart.getData().size(); i++)
				{
					int idx = chart.getIndexAt(i, x);

					for (SelectionListener sl : selectionListeners)
					{
						sl.selected(i, idx);
					}
				}
			}
		};
		
		chart.addMouseListener(ma);
		chart.addMouseMotionListener(ma);
		
		//Create the toolbar.
	    JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
	    
	    toolBar.setFloatable(false);
	    toolBar.setRollover(true);
	    
	    JToggleButton distanceButton = new JToggleButton(new ImageIcon("images/distance.png"));
	    JToggleButton heightButton = new JToggleButton(new ImageIcon("images/height.png"));
	    JToggleButton speedButton = new JToggleButton(new ImageIcon("images/speed.png"));
	    JToggleButton timeButton = new JToggleButton(new ImageIcon("images/time.png"));
	    
	    ButtonGroup bgv = new ButtonGroup();
	    bgv.add(distanceButton);
	    bgv.add(timeButton);
	    
	    ButtonGroup bgh = new ButtonGroup();
	    bgh.add(heightButton);
	    bgh.add(speedButton);
	    
	    distanceButton.setToolTipText("Distance");
		heightButton.setToolTipText("Height");
		speedButton.setToolTipText("Speed");
		timeButton.setToolTipText("Time");
		
		distanceButton.getModel().setSelected(chartModeHorz == ChartModeHorz.Distance);
		timeButton.getModel().setSelected(chartModeHorz == ChartModeHorz.Time);
		heightButton.getModel().setSelected(chartModeVert == ChartModeVert.Height);
		speedButton.getModel().setSelected(chartModeVert == ChartModeVert.Speed);
		
		toolBar.addSeparator();
	    toolBar.add(distanceButton);
		toolBar.add(timeButton);
		toolBar.addSeparator();
		toolBar.add(heightButton);
		toolBar.add(speedButton);
		
		distanceButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				chartModeHorz = ChartModeHorz.Distance;
				reload();
			}
		});
		
		heightButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				chartModeVert = ChartModeVert.Height;
				reload();
			}
		});
		
		speedButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				chartModeVert = ChartModeVert.Speed;
				reload();
			}
		});
	    
		timeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				chartModeHorz = ChartModeHorz.Time;
				reload();
			}
		});
		
	    setLayout(new BorderLayout());
		add(toolBar, BorderLayout.WEST);
		add(chart);
	}
	
	/**
	 * @param tracks the list of tracks to display
	 */
	public void setTracks(List<Track> tracks)
	{
		this.tracks = new ArrayList<Track>(tracks);
		
		reload();
	}
	
	/**
	 * @param sl the selection listener
	 */
	public void addSelectionListener(SelectionListener sl)
	{
		selectionListeners.add(sl);
	}
	
	
	/**
	 * @param sl the selection listener
	 */
	public void removeSelectionListener(SelectionListener sl)
	{
		selectionListeners.remove(sl);
	}
	
	private void reload()
	{
		List<List<Point2D>> data = new ArrayList<List<Point2D>>();
		
		if (tracks.isEmpty())
			return;
		
		for (Track track : tracks)
		{
			List<Point2D> pts = new ArrayList<Point2D>();
			
			for (TrackPoint trackPt : track.getPoints())
			{
				pts.add(chartPointFromTrackPoint(trackPt));
			}
			
			data.add(pts);
		}
		
		updateChartLabels();
		chart.setData(data);
	}

	private void updateChartLabels()
	{
		switch (chartModeVert)
		{
			case Height:
				chart.setVertDesc("m");
				break;

			case Speed:
				chart.setVertDesc("km/h");
				break;
		}

		switch (chartModeHorz)
		{
			case Distance:
				chart.setHorzDesc("km");
				break;

			case Time:
				chart.setHorzDesc("min");
				break;
		}
	}
	
	private Point2D chartPointFromTrackPoint(TrackPoint trackPt)
	{
		double x = 0;
		double y = 0;

		switch (chartModeVert)
		{
			case Height:
				y = trackPt.getElevation();
				break;

			case Speed:
				y = trackPt.getSpeed();
				break;
		}
		
		switch (chartModeHorz)
		{
			case Distance:
				x = trackPt.getDistance() * 0.001;
				break;

			case Time:
				x = trackPt.getTime().getTime()  / 60000.0;
				break;
		}

		return new Point2D.Double(x, y);
	}
}
