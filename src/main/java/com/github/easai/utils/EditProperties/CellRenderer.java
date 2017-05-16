package com.github.easai.utils.EditProperties;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable t, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if ( column == 0) {
			setBackground(new Color(0xea, 0xeb, 0xff));
		} else {
			setBackground(new Color(0xff, 0xff, 0xff));
		}

		
		return super.getTableCellRendererComponent(t, value, isSelected,
				hasFocus, row, column);
	}
}
