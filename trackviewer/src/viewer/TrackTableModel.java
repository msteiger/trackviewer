
package viewer;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import track.Track;

/**
 * TODO Type description
 * @author Martin Steiger
 */
final class TrackTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 819860756869723997L;
	private final List<Track> tracks;
	private String[] columnNames = { "Name", "Distance"};

	/**
	 * @param tracks the list of tracks
	 */
	public TrackTableModel(List<Track> tracks)
	{
		this.tracks = tracks;
	}

	@Override
	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	@Override
	public int getRowCount()
	{
		return tracks.size();
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		Track track = tracks.get(row);

		switch (col)
		{
		case 0:
			return track.getName();
			
		case 1:
			return track.getTotalDistance();
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