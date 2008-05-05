package nz.ac.massey.cs.barrio.gui;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	CheckBoxRenderer() {
	    setHorizontalAlignment(JLabel.CENTER);
	  }

	  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    if (isSelected) {
	      setForeground(table.getSelectionForeground());
	      setBackground(table.getSelectionBackground());
	    } else {
	      setForeground(table.getForeground());
	      setBackground(table.getBackground());
	    }
	    setSelected((value != null && ((Boolean) value).booleanValue()));
	    this.setName(String.valueOf(row));
	    this.setToolTipText("Highlight edge in visual dependency graph");
	    return this;
	  }
}
