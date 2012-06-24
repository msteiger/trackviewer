
package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.bind.JAXBException;

import main.table.DistanceFormat;
import main.table.FormatRenderer;
import main.table.JShadedTable;
import main.table.SpeedFormat;
import main.table.TimeFormat;
import main.table.TrackTableModel;
import tcx.TcxAdapter;
import track.Track;

/**
 * A simple sample application that shows a OSM map of Europe
 * @author Martin Steiger
 */
public class MainFrame extends JFrame
{
	private static final long serialVersionUID = -9215006987029836062L;
	private MapViewer viewer;
	private JTable table;
	private TrackChart trackChart;

	/**
	 * Constructs a new instance
	 */
	public MainFrame()
	{
		super("TrackViewer");
		
		File folder = new File(System.getProperty("user.home") + File.separator + "trackviewer");

		List<Track> tracks = readTracks(folder);

		viewer = new MapViewer();

		table = createTable(tracks);

		// put in a scrollpane to add scroll bars
		JScrollPane tablePane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		trackChart = new TrackChart();
				
		//Create the main split pane 
		JSplitPane chartSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, viewer, trackChart);
		chartSplitPane.setDividerLocation(550);

		//Create the main split pane 
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePane, chartSplitPane);
		mainSplitPane.setDividerLocation(230);

		//Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(100, 50);
		tablePane.setMinimumSize(minimumSize);
		chartSplitPane.setMinimumSize(minimumSize);
		
		add(createMenu(), BorderLayout.NORTH);
		add(mainSplitPane);
		
		table.getSelectionModel().setSelectionInterval(0, 0);
	}

	
	private JTable createTable(final List<Track> tracks)
	{
		TrackTableModel model = new TrackTableModel(tracks);

		final JTable table = new JShadedTable(model);

		// Workaround to separate IDs from labels
		// By default, ID is not set or used by JTable
		// but the columnModel uses it. If not available it uses
		// the ID that is defined by the TableModel
		// So, the ID must be explicitly set for the columnModel to continue
		// to work.
		String[] labels = model.getColumnLabels();
		for (int i = 0; i < model.getColumnCount(); i++)
		{
			table.getColumnModel().getColumn(i).setIdentifier(model.getColumnName(i));
			table.getColumnModel().getColumn(i).setHeaderValue(labels[i]);
		}
		
		// set formatting of columns
		FormatRenderer dateRenderer = new FormatRenderer(SimpleDateFormat.getDateTimeInstance(), SwingConstants.LEFT);
		FormatRenderer distanceRenderer = new FormatRenderer(new DistanceFormat());
		FormatRenderer timeRenderer = new FormatRenderer(new TimeFormat());
		FormatRenderer speedRenderer = new FormatRenderer(new SpeedFormat());
		
		table.getColumn("date").setCellRenderer(dateRenderer);
		table.getColumn("distance").setCellRenderer(distanceRenderer);
		table.getColumn("time").setCellRenderer(timeRenderer);
		table.getColumn("speed").setCellRenderer(speedRenderer);

		// Set row sorter
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);

		// Set selection model
		table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListMultiSelectionListener()
		{
			@Override
			public void valueChanged(List<Integer> indices)
			{
				List<Track> selTracks = new ArrayList<Track>();
				
				for (Integer idx : indices)
				{
					idx = table.convertRowIndexToModel(idx);
					selTracks.add(tracks.get(idx));
				}
				
				viewer.showRoute(selTracks);
				trackChart.setTracks(selTracks);
			}
		});
		
		return table;
	}

	private List<Track> readTracks(File folder)
	{
		List<Track> tracks = new ArrayList<Track>();

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
			return tracks;
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
						tracks.add(t);
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
		
		for (Track track : tracks)
		{
			TrackComputer.repairTrackData(track);
		}
		
		return tracks;
	}

	private JMenuBar createMenu()
	{
		//Create the menu bar.
		JMenuBar menuBar = new JMenuBar();

		//Build the first menu.
		JMenu menu = new JMenu("A Menu");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
		        "The only menu in this program that has menu items");
		menuBar.add(menu);

		//a group of JMenuItems
		JMenuItem menuItem = new JMenuItem("A text-only menu item",
		                         KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
		        "This doesn't really do anything");
		menu.add(menuItem);

		menuItem = new JMenuItem("Both text and icon",
		                         new ImageIcon("images/middle.gif"));
		menuItem.setMnemonic(KeyEvent.VK_B);
		menu.add(menuItem);

		menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
		menuItem.setMnemonic(KeyEvent.VK_D);
		menu.add(menuItem);

		//a group of radio button menu items
		menu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
		rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_R);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Another one");
		rbMenuItem.setMnemonic(KeyEvent.VK_O);
		group.add(rbMenuItem);
		menu.add(rbMenuItem);

		//a group of check box menu items
		menu.addSeparator();
		JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
		cbMenuItem.setMnemonic(KeyEvent.VK_C);
		menu.add(cbMenuItem);

		cbMenuItem = new JCheckBoxMenuItem("Another one");
		cbMenuItem.setMnemonic(KeyEvent.VK_H);
		menu.add(cbMenuItem);

		//a submenu
		menu.addSeparator();
		JMenu submenu = new JMenu("A submenu");
		submenu.setMnemonic(KeyEvent.VK_S);

		menuItem = new JMenuItem("An item in the submenu");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_2, ActionEvent.ALT_MASK));
		submenu.add(menuItem);

		menuItem = new JMenuItem("Another item");
		submenu.add(menuItem);
		menu.add(submenu);

		//Build second menu in the menu bar.
		menu = new JMenu("Another Menu");
		menu.setMnemonic(KeyEvent.VK_N);
		menu.getAccessibleContext().setAccessibleDescription(
		        "This menu does nothing");
		menuBar.add(menu);
		
		return menuBar;
	}
	
	/**
	 * @param args the program args (ignored)
	 */
	public static void main(String[] args)
	{
		JFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 768);
		frame.setVisible(true);
	}
}
