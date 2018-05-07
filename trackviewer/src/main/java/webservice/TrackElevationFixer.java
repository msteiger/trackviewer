package webservice;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import track.Track;
import track.TrackPoint;

/**
 * Fixes all elevations in a .tcx file using the {@link ElevationFixer} class.
 *
 * @author Martin Steiger
 */
public class TrackElevationFixer {

    private static final Log log = LogFactory.getLog(TrackElevationFixer.class);

    /**
     * @param track the track to fix
     */
    public static void fixTrack(Track track) {
        List<Double> elevations;
        try {
            elevations = ElevationFixer.getElevations(track.getRoute());
            List<TrackPoint> points = track.getPoints();

            for (int i = 0; i < points.size(); i++) {
                points.get(i).setElevation(elevations.get(i));
            }

            log.info("Updated " + points.size() + " elevations");
        } catch (IOException e) {
            log.error("Error converting " + track, e);
        }
    }

}
