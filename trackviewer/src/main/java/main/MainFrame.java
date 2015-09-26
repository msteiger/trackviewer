package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import main.chart.StatusBar;
import main.table.DistanceFormat;
import main.table.FormatRenderer;
import main.table.JShadedTable;
import main.table.SpeedFormat;
import main.table.TimeFormat;
import main.table.TrackTableModel;
import track.Track;
import webservice.TrackElevationFixer;

/**
 * A simple sample application that shows a OSM map of Europe
 *
 * @author Martin Steiger
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = -9215006987029836062L;
    private MapViewer viewer;
    private JTable table;
    private StatusBar statusBar;
    private TrackChart trackChart;

    /**
     * Constructs a new instance
     */
    public MainFrame(String tracksdir) {
        super("TrackViewer");

        File folder;
        
        if(tracksdir == null) {
            folder = new File(System.getProperty("user.home") + File.separator
                + "trackviewer");
        } else {
            folder = new File(tracksdir);
        }

        final List<Track> tracks = new CopyOnWriteArrayList<>();

        viewer = new MapViewer();

        table = createTable(tracks);

        TrackLoader.readTracks(folder, new TrackLoadListener() {
            @Override
            public void trackLoaded(Track track) {
                tracks.add(track);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ((AbstractTableModel) table.getModel()).fireTableDataChanged();
                    }
                });
            }
        });

        // put in a scrollpane to add scroll bars
        JScrollPane tablePane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        trackChart = new TrackChart();

        trackChart.addSelectionListener(new SelectionListener() {
            @Override
            public void selected(int series, int index) {
                viewer.setMarker(series, index);
            }
        });

        statusBar = new StatusBar();
        add(statusBar, BorderLayout.SOUTH);

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

        add(createMenu(tracks), BorderLayout.NORTH);
        add(mainSplitPane);

//		table.getSelectionModel().setSelectionInterval(0, 0);
    }

    private JTable createTable(final List<Track> tracks) {
        TrackTableModel model = new TrackTableModel(tracks);

        final JTable table = new JShadedTable(model);

		// Workaround to separate IDs from labels
        // By default, ID is not set or used by JTable
        // but the columnModel uses it. If not available it uses
        // the ID that is defined by the TableModel
        // So, the ID must be explicitly set for the columnModel to continue
        // to work.
        String[] labels = model.getColumnLabels();
        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setIdentifier(model.getColumnName(i));
            table.getColumnModel().getColumn(i).setHeaderValue(labels[i]);
        }

        // set formatting of columns
        FormatRenderer dateRenderer = new FormatRenderer(SimpleDateFormat.getDateTimeInstance(), SwingConstants.LEFT);
        FormatRenderer distanceRenderer = new FormatRenderer(new DistanceFormat());
        FormatRenderer timeRenderer = new FormatRenderer(new TimeFormat());
        FormatRenderer speedRenderer = new FormatRenderer(new SpeedFormat());
        FormatRenderer altiRenderer = new FormatRenderer(new DecimalFormat("# m"));

        table.getColumn("date").setCellRenderer(dateRenderer);
        table.getColumn("distance").setCellRenderer(distanceRenderer);
        table.getColumn("time").setCellRenderer(timeRenderer);
        table.getColumn("speed").setCellRenderer(speedRenderer);
        table.getColumn("altitude").setCellRenderer(altiRenderer);

        // Set row sorter
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        sorter.toggleSortOrder(0);		// sorts ascending
        sorter.toggleSortOrder(0);		// sorts descending

        // Set selection model
        table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListMultiSelectionListener() {
            @Override
            public void valueChanged(List<Integer> indices) {
                List<Track> selTracks = new ArrayList<>();

                for (Integer idx : indices) {
                    idx = table.convertRowIndexToModel(idx);
                    selTracks.add(tracks.get(idx));
                }

                viewer.showRoute(selTracks);
                trackChart.setTracks(selTracks);
            }
        });

        return table;
    }

    private JMenuBar createMenu(List<Track> tracks) {
        //Create the menu bar.
        JMenuBar menuBar = new JMenuBar();

        //Build the first menu.
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        menu.add(exportTrackItem(tracks));
        menu.addSeparator();
        menu.add(fixElevationItem(tracks));
        menu.add(insertGapsItem(tracks));

        //a group of radio button menu items
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

        //Build second menu in the menu bar.
        menu = new JMenu("Another Menu");
        menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);

        return menuBar;
    }

    private JMenuItem exportTrackItem(final List<Track> tracks) {
        JMenuItem menuItem = new JMenuItem(new AbstractAction() {
            private static final long serialVersionUID = -3691668348789171952L;

            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = table.getSelectedRow();
                idx = table.convertRowIndexToModel(idx);

                exportToFile(tracks.get(idx));
            }
        });

        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
        menuItem.setText("Export data");
        menuItem.setMnemonic(KeyEvent.VK_E);

        return menuItem;
    }

    private void exportToFile(Track track) {
//		JDialog ...		
        try {
            TrackLoader.saveAsGpx("E:\\fixed.gpx", track);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JMenuItem fixElevationItem(final List<Track> tracks) {
        //a group of JMenuItems
        JMenuItem menuItem = new JMenuItem(new AbstractAction() {
            private static final long serialVersionUID = -3691668348789171952L;

            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = table.getSelectedRow();
                idx = table.convertRowIndexToModel(idx);

                TrackElevationFixer.fixTrack(tracks.get(idx));
            }
        });

        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
        menuItem.setText("Fix Elevantion");
        menuItem.setMnemonic(KeyEvent.VK_E);

        return menuItem;
    }

    private JMenuItem insertGapsItem(final List<Track> tracks) {
        //a group of JMenuItems
        JMenuItem menuItem = new JMenuItem(new AbstractAction() {
            private static final long serialVersionUID = -3691668348789171952L;

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] idx = table.getSelectedRows();

                if (idx.length != 2) {
                    return;
                }

                int idx1 = table.convertRowIndexToModel(idx[0]);
                int idx2 = table.convertRowIndexToModel(idx[1]);

                GapInserter.insertGaps(tracks.get(idx1), tracks.get(idx2));
            }
        });

        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.ALT_MASK));
        menuItem.setText("Insert Gaps");
        menuItem.setMnemonic(KeyEvent.VK_G);

        return menuItem;
    }

    /**
     * @param args the program args (ignored)
     */
    public static void main(String[] args) {
        String tracksdir = null;
        if(args.length > 0) {
            tracksdir = args[args.length - 1];
        }
        JFrame frame = new MainFrame(tracksdir);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setVisible(true);
    }
}
