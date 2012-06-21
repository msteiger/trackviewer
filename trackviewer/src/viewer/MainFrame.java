
package viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
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
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;
import javax.xml.bind.JAXBException;

import tcx.TcxAdapter;
import track.Track;

/**
 * A simple sample application that shows a OSM map of Europe
 * @author Martin Steiger
 */
public class MainFrame extends JFrame
{
	private static final long serialVersionUID = -9215006987029836062L;

	/**
	 * Constructs a new instance
	 */
	public MainFrame()
	{
		super("TrackViewer");
		
		File folder = new File(System.getProperty("user.home") + File.separator + "trackviewer");

		List<Track> tracks = readTracks(folder);

		MapViewer viewer = new MapViewer();
		viewer.setRoute(tracks.get(0));

		add(createMenu(), BorderLayout.NORTH);
		add(viewer);
		add(createTable(tracks), BorderLayout.WEST);
	}

	private Component createTable(List<Track> tracks)
	{
		TableModel model = new TrackTableModel(tracks);

		JTable table = new JShadedTable(model);
		FormatRenderer distanceRenderer = new FormatRenderer(new DistanceFormat());
		table.getColumn("Distance").setCellRenderer(distanceRenderer);
		
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		return scrollPane;
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
				tracks.addAll(tcxAdapter.read(fis));
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
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
