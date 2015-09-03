package main;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A convenience wrapper that extracts the list of selected indices from the
 * selection model in a similar way JTable does.
 *
 * @author Martin Steiger
 */
public abstract class ListMultiSelectionListener implements ListSelectionListener {

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        List<Integer> sel = new ArrayList<Integer>();

        ListSelectionModel model = (ListSelectionModel) e.getSource();

	// The events values mirror only the current 
        // change and not the full selection
	// int iMin = e.getFirstIndex();
        // int iMax = e.getLastIndex();
        int iMin = model.getMinSelectionIndex();
        int iMax = model.getMaxSelectionIndex();

        for (int i = iMin; i <= iMax; i++) {
            if (model.isSelectedIndex(i)) {
                sel.add(i);
            }
        }

        valueChanged(sel);
    }

    /**
     * @param indices the list of selected indices
     */
    protected abstract void valueChanged(List<Integer> indices);
}
