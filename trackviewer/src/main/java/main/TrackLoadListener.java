package main;

import track.Track;

/**
 * A notification callback for loaded tracks
 *
 * @author Martin Steiger
 */
public interface TrackLoadListener {

    /**
     * @param track the track
     */
    public void trackLoaded(Track track);

}
