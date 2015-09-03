package common;

import java.util.HashMap;
import java.util.Map;

/**
 * A very simple profiler
 *
 * @author Martin Steiger
 */
public class Profiler {

    private static final Map<Object, Long> map = new HashMap<Object, Long>();

    /**
     * @param id an identifier object
     */
    public static void start(Object id) {
        Long time = measure();
        map.put(id, time);
    }

    /**
     * @param id an identifier object
     * @return the time milliseconds
     */
    public static double get(Object id) {
        Long start = map.get(id);
        Long time = measure();

        if (start == null) {
            throw new IllegalArgumentException("id");
        }

        return (time - start) / 1000000.0;
    }

    /**
     * @param id an identifier object
     * @return the time in milliseconds as formatted string
     */
    public static String getString(Object id) {
        double time = get(id);

        return String.format("%6.2fms.", time);
    }

    private static Long measure() {
        return System.nanoTime();
    }
}
