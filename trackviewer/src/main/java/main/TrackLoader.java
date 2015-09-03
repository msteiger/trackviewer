package main;

import gpx.GpxAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tcx.TcxAdapter;
import track.Track;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;

/**
 * Loads a series of track files from a folder in an asynchronous manner.
 *
 * @author Martin Steiger
 */
public class TrackLoader {

    private static final Log log = LogFactory.getLog(TrackLoader.class);

    /**
     * @param folder the folder that contains the track files
     * @param cb the callback
     */
    public static void readTracks(final File folder, final TrackLoadListener cb) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                fill(folder, cb);
            }
        });

        th.start();
    }

    private static void fill(File folder, TrackLoadListener cb) {
        String[] files = folder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".tcx") || name.endsWith(".gpx");
            }
        });

        TcxAdapter tcxAdapter = null;
        GpxAdapter gpxAdapter = null;

        try {
            tcxAdapter = new TcxAdapter();
        } catch (JAXBException e) {
            JOptionPane.showMessageDialog(null, e);
            log.error("Error initializing TcxAdapter", e);
            return;
        }
        try {
            gpxAdapter = new GpxAdapter();
        } catch (JAXBException e) {
            JOptionPane.showMessageDialog(null, e);
            log.error("Error initializing GpxAdapter", e);
            return;
        }

        for (String fname : files) {
            FileInputStream fis = null;

            try {
                fis = new FileInputStream(new File(folder, fname));
                if (fname.toLowerCase().endsWith(".tcx")) {
                    TrainingCenterDatabaseT data = tcxAdapter.unmarshallObject(fis);
                    List<Track> read = tcxAdapter.convertToTracks(data);

                    for (Track t : read) {
                        // skip empty tracks
                        if (!t.getPoints().isEmpty()) {
                            TrackComputer.repairTrackData(t);
                            cb.trackLoaded(t);
                        }
                    }
                } else if (fname.toLowerCase().endsWith(".gpx")) {
                    List<Track> read = gpxAdapter.read(fis);
                    for (Track t : read) {
                        // skip empty tracks
                        if (!t.getPoints().isEmpty()) {
                            TrackComputer.repairTrackData(t);
                            cb.trackLoaded(t);
                        }
                    }
                }

                log.debug("Loaded " + fname);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                log.error(e.getMessage(), e);
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }

    }

    /**
     * @param fname the filename
     * @param track the track data
     * @throws IOException if something goes wrong
     */
    public static void saveAsGpx(String fname, Track track) throws IOException {
        OutputStream os = null;

        try {
            os = new FileOutputStream(fname);
            GpxAdapter gpxAdapter = new GpxAdapter();
            gpxAdapter.write(os, Collections.singletonList(track));
        } catch (JAXBException e) {
            throw new IOException(e);
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

}
