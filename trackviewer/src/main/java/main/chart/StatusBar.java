package main.chart;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 * A simple status bar
 *
 * @author Martin Steiger
 */
public class StatusBar extends JPanel {

    private static final long serialVersionUID = 706957128534249767L;
    private JLabel statusLabel;
    private JLabel extraLabel;

    /**
     * Default constructor
     */
    public StatusBar() {
        setPreferredSize(new Dimension(16, 16));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        statusLabel = new JLabel("Ready");
        add(statusLabel);

        add(Box.createHorizontalGlue());

        extraLabel = new JLabel();
        extraLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(extraLabel);
        extraLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        add(Box.createHorizontalStrut(50));
    }

    /**
     * @param text the text
     */
    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    /**
     * @param text the text
     */
    public void setExtra(String text) {
        extraLabel.setText(text);
    }
}
