package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.jxmapviewer.viewer.GeoPosition;

import tcx.TcxAdapter;
import track.Track;
import track.TrackPoint;

import com.garmin.xmlschemas.trainingcenterdatabase.v2.TrainingCenterDatabaseT;
import common.GeoUtils;

/**
 * TODO Type description
 *
 * @author Martin Steiger
 */
public class GapInserter {

    private static final File folder = new File(System.getProperty("user.home")
            + File.separator + "trackviewer");

    public static void main(String[] args) throws IOException, JAXBException {
        FileInputStream fis1 = new FileInputStream(new File(folder, "2012-06-26-16-51-03_fix.tcx"));
        FileInputStream fis2 = new FileInputStream(new File(folder, "2012-06-10-12-25-41_fix.tcx"));

        TcxAdapter ta = new TcxAdapter();

        TrainingCenterDatabaseT file1 = ta.unmarshallObject(fis1);
        TrainingCenterDatabaseT file2 = ta.unmarshallObject(fis2);

        List<Track> tracks1 = ta.convertToTracks(file1);
        List<Track> tracks2 = ta.convertToTracks(file2);

        Track track1 = tracks1.get(0);
        Track track2 = tracks2.get(0);

        TrackComputer.repairTrackData(track1);
        TrackComputer.repairTrackData(track2);

        insertGaps(track1, track2);

//		[230..630] - 11094m
//		Closest index in other track: 240 -- 530
//		[970..986] - 710m
//		Closest index in other track: 879 -- 898
//		--------------------------------------------
//		[243..527] - 7537m
//		Closest index in other track: 227 -- 632
//		[881..896] - 642m
//		Closest index in other track: 968 -- 986
    }

    public static void insertGaps(Track track1, Track track2) {
        double sepVal = 70;

        List<Range<Integer>> gaps1 = findGaps(track1, track2, sepVal);
        List<Range<Integer>> gaps2 = findGaps(track2, track1, sepVal);

	//	removeOverlaps(track1, track2, gaps1, gaps2);
        System.out.println("--------------------------------------------");

        asdf(gaps1, track1, track2);

        System.out.println("--------------------------------------------");

        asdf(gaps2, track2, track1);
    }

    private static void asdf(List<Range<Integer>> gaps1, Track track1, Track track2) {

        List<TrackPoint> pts1 = track1.getPoints();
        List<TrackPoint> pts2 = track2.getPoints();

        for (Range<Integer> range : gaps1) {
            double gapDist = pts1.get(range.getEnd()).getDistance()
                    - pts1.get(range.getStart()).getDistance();
            long gapTime = pts2.get(range.getEnd()).getTime().getTime()
                    - pts2.get(range.getStart()).getTime().getTime();

            System.out.println("[" + range.getStart() + ".." + range.getEnd() + "] - " + (int)gapDist + "m");

            int otherStartIdx = findClosestPointIndex(pts1.get(range.getStart()).getPos(), track2.getRoute());
            int otherEndIdx = findClosestPointIndex(pts1.get(range.getEnd()).getPos(), track2.getRoute());

            System.out.println("Closest index in other track: " + otherStartIdx + " -- " + otherEndIdx);

            double overlapDist = pts2.get(otherEndIdx).getDistance()
                    - pts2.get(otherStartIdx).getDistance();
            long overlapTime = pts2.get(otherEndIdx).getTime().getTime()
                    - pts2.get(otherStartIdx).getTime().getTime();

            double totalDistGap = gapDist - overlapDist;
            long totalTimeGap = gapTime - overlapTime;

            if (totalDistGap > 0 && totalTimeGap > 0) {
                System.out.println("Inserting gap of " + (int)totalDistGap + "m at " + otherStartIdx);
                insertGap(track2.getPoints(), otherStartIdx, totalDistGap, totalTimeGap);
            }
        }

    }
//
//	private static void removeOverlaps(Track track1, Track track2, List<Range<Integer>> gaps1, List<Range<Integer>> gaps2)
//	{
//		List<TrackPoint> pts1 = track1.getPoints();
//		List<TrackPoint> pts2 = track2.getPoints();
//
//		for (Range<Integer> range1 : gaps1)
//		{
//			int otherStartIdx = findClosestPointIndex(pts1.get(range1.getStart()).getPos(), track2.getRoute());
//	//		int otherEndIdx = findPointAtDistance(track2.getPoints(), track2.getPoints().get(otherStartIdx).getDistance() + gap); 
//			int otherEndIdx = findClosestPointIndex(pts1.get(range1.getEnd()).getPos(), track2.getRoute()); 
//
//			Range range1t = new Range<Integer>(otherStartIdx, otherEndIdx);
//			
//			reduceOverlap(pts2, range1t, gaps2);
//		}
//	}
//
//	private static double reduceOverlap(List<TrackPoint> points, Range<Integer> other, List<Range<Integer>> gaps)
//	{
//		double total = 0;
//		int start = other.getStart();
//		int end = other.getEnd();
//		
//		for (Range<Integer> range : gaps)
//		{
//			int rs = range.getStart();
//			int re = range.getEnd();
//
//			if ((rs > start && rs < end) &&		// range start is inside
//				(re > start && re < end))		// range end is inside
//			{
//				double dist = points.get(re).getDistance() - points.get(rs).getDistance();
//
//				System.out.println("Fully inside: " + rs +  "-" + re);
//			}
//			else
//			if (rs > start && rs < end)		// range start is inside
//			{
//				double dist = points.get(end).getDistance() - points.get(rs).getDistance();
//				total += dist;
//				
//				System.out.println("Start inside: " + rs +  "-" + re);
//			}
//			else
//			if (re > start && re < end)		// range end is inside
//			{
//				double dist = points.get(start).getDistance() - points.get(rs).getDistance();
//				total += dist;
//				
//				System.out.println("End inside: " + rs +  "-" + re);
//			}		
//		}
//		
//		return total;
//	}
//
//	private static int findPointAtDistance(List<TrackPoint> points, double dist)
//	{
//		for (int i = 0; i < points.size(); i++)
//		{
//			TrackPoint pt = points.get(i);
//			
//			if (pt.getDistance() > dist)
//				return i;
//		}
//		
//		return -1;
//	}

    private static void insertGap(List<TrackPoint> points, int idx, double dist, long time) {
        for (int i = idx; i < points.size(); i++) {
            TrackPoint pt = points.get(i);
            pt.setDistance(pt.getDistance() + dist);
            pt.setTime(new Date(pt.getTime().getTime() + time));
        }
    }

    private static List<Range<Integer>> findGaps(Track track1, Track track2, double sepVal) {
        List<GeoPos> geoList1 = new ArrayList<>();
        List<GeoPos> geoList2 = new ArrayList<>();

        for (GeoPosition pos : track1.getRoute()) {
            geoList1.add(new GeoPos(pos.getLatitude(), pos.getLongitude()));
        }

        for (GeoPosition pos : track2.getRoute()) {
            geoList2.add(new GeoPos(pos.getLatitude(), pos.getLongitude()));
        }

        return findGapsGeo(geoList1, geoList2, sepVal);
    }

    private static List<Range<Integer>> findGapsGeo(List<GeoPos> track1, List<GeoPos> track2, double sepVal) {
        List<Range<Integer>> result = new ArrayList<>();

        boolean inside = false;

        int start = -1;
        int end = -1;

        for (int i = 0; i < track1.size(); i++) {
            GeoPos pos1 = track1.get(i);

            double dist = GeoPos.distanceToPolyMtrs(track2, pos1);

//			System.out.println(dist);
            if (dist > sepVal) {
                if (!inside) {
                    inside = true;
                    start = i;
                }
            } else if (inside) {
                inside = false;
                end = i;

                result.add(new Range<>(start, end));
            }
        }

        return result;
    }

    private static int findClosestPointIndex(GeoPosition pos1, List<GeoPosition> route) {
        double minVal = Double.MAX_VALUE;
        int minInd = Integer.MAX_VALUE;

        for (int i = 0; i < route.size(); i++) {
            double dist = GeoUtils.computeDistance(pos1, route.get(i));

            if (dist < minVal) {
                minVal = dist;
                minInd = i;
            }
        }

        return minInd;
    }

}
