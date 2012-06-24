
package main.table;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import track.Track;

/**
 * A table model for {@link Track}s
 * @author Martin Steiger
 */
public final class TrackTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 819860756869723997L;
	private final List<Track> tracks;
	private final String[] columnIds = { "date", "distance", "speed" };
	private final String[] columnLabels = { "Date", "Distance (km)", "Average Speed (km/h)"};
	private final Class<?>[] columnClass = { Date.class, Double.class, Double.class };

	/**
	 * @param tracks the list of tracks
	 */
	public TrackTableModel(List<Track> tracks)
	{
		this.tracks = tracks;
	}
	
	/**
	 * @return the columnLabels
	 */
	public String[] getColumnLabels()
	{
		return columnLabels;
	}



	@Override
	public String getColumnName(int col)
	{
		return columnIds[col];
	}
	
	@Override
	public Class<?> getColumnClass(int col)
	{
		return columnClass[col];
	}

	@Override
	public int getRowCount()
	{
		return tracks.size();
	}

	@Override
	public int getColumnCount()
	{
		return columnIds.length;
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		Track track = tracks.get(row);

		switch (col)
		{
		case 0:
			return track.getStartTime();
			
		case 1:
			return track.getTotalDistance();
			
		case 2:
			return track.getAverageSpeed();
		}
		
		return track;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

	@Override
	public void setValueAt(Object value, int row, int col)
	{
//		fireTableCellUpdated(row, col);
	}
}