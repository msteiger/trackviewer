package main;

/**
 * Fired whenever a selection has changed
 *
 * @author Martin Steiger
 */
public interface SelectionListener {

    /**
     * @param series the data series
     * @param index the index in the series
     */
    public void selected(int series, int index);
}
