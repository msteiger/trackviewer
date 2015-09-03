package main.table;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * A JTable with alternating row colors
 *
 * @author Martin Steiger
 */
public class JShadedTable extends JTable {

    private static final long serialVersionUID = 8439404405306590238L;

    private Color shadeColor = new Color(240, 240, 240);

    /**
     * @see JTable#JTable()
     */
    public JShadedTable() {
        super();
    }

    /**
     * @see JTable#JTable(TableModel)
     * @param dm the data model for the table
     */
    public JShadedTable(TableModel dm) {
        super(dm);
    }

    /**
     * @see JTable#JTable(int, int)
     * @param numRows the number of rows the table holds
     * @param numColumns the number of columns the table holds
     */
    public JShadedTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    /**
     * @see JTable#JTable(Object[][], Object[])
     * @param rowData the data for the new table
     * @param columnNames names of each column
     */
    public JShadedTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    /**
     * @see JTable#JTable(TableModel, TableColumnModel, ListSelectionModel)
     * @param dm the data model for the table
     * @param cm the column model for the table
     * @param sm the row selection model for the table
     */
    public JShadedTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    /**
     * @see JTable#JTable(TableModel, TableColumnModel)
     * @param dm the data model for the table
     * @param cm the column model for the table
     */
    public JShadedTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    /**
     * @see JTable#JTable(Vector, Vector)
     * @param rowData the data for the new table
     * @param columnNames names of each column
     */
    public JShadedTable(Vector<?> rowData, Vector<?> columnNames) {
        super(rowData, columnNames);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component comp = super.prepareRenderer(renderer, row, column);

        if (!isCellSelected(row, column)) {
            comp.setBackground(row % 2 == 0 ? getBackground() : shadeColor);
        }

        return comp;
    }

    /**
     * @return the shadeColor
     */
    public Color getShadeColor() {
        return shadeColor;
    }

    /**
     * @param shadeColor the shadeColor to set
     */
    public void setShadeColor(Color shadeColor) {
        this.shadeColor = shadeColor;
    }

}
