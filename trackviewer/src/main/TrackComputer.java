
package main;

import java.util.Date;
import java.util.List;

import common.GeoUtils;

import track.Track;
import track.TrackPoint;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class TrackComputer
{
	/**
	 * @param track fill track with missing data
	 */
	public static void repairTrackData(Track track)
	{
		fixNonNullStarts(track);
		fixDistances(track);
		fixTimes(track);
		
		for (int i=0; i<track.getPoints().size(); i++)
		{
			computeSpeed(track, i);
		}
	}

	private static void fixTimes(Track track)
	{
		List<TrackPoint> points = track.getPoints();

		if (points.isEmpty())
			return;
		
		long start = points.get(0).getTime().getTime();

		for (TrackPoint point : points)
		{
			long time = point.getTime().getTime();
			point.getTime().setTime(time - start);
		}
		
		track.setStartTime(new Date(start));
	}

	private static void fixDistances(Track track)
	{
		List<TrackPoint> points = track.getPoints();

		if (points.isEmpty())
			return;

		TrackPoint prevPoint = points.get(0);

		for (TrackPoint point : points)
		{
			if (point.getDistance() <= prevPoint.getDistance())
			{
				double delta = GeoUtils.computeDistance(prevPoint.getPos(), point.getPos());
				double dist = prevPoint.getDistance() + delta;
				point.setDistance(dist);
			}

			prevPoint = point;
		}
	}

	private static void fixNonNullStarts(Track track)
	{
		List<TrackPoint> points = track.getPoints();

		if (points.isEmpty())
			return;

		double offset = points.get(0).getDistance();

		if (Math.abs(offset) < 0.01)
			return;

		for (TrackPoint point : points)
		{
			point.setDistance(point.getDistance() - offset);
		}
	}

	private static void computeSpeed(Track track, int index)
	{
		final int range = 2;

		List<TrackPoint> points = track.getPoints();

		// compute speed from [-range..range] around index
		int lowBound = Math.max(index - range, 0);
		int highBound = Math.min(index + range, points.size() - 1);

		TrackPoint high = points.get(highBound);
		TrackPoint low = points.get(lowBound);

		double deltaDistance = high.getDistance() - low.getDistance();	     // meters
		long deltaTime = high.getTime().getTime() - low.getTime().getTime(); // milliseconds

		if (deltaTime != 0)
		{
			points.get(index).setSpeed(deltaDistance * 3600.0 / deltaTime);
		}
	}
}
