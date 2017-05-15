package com.github.easai.utils;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class EditProperties extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Properties properties = null;
	int nProperties = 0;
	private JScrollPane scroll = new JScrollPane();
	String header[] = { "Property", "Values" };
	private JTable table = new JTable(null, header);
	private JMenuBar menuBar = new JMenuBar();
	CellRenderer cellRenderer = new CellRenderer();
	JButton cancel = new JButton("Cencel");
	JButton save = new JButton("Save");
	String propertyFile = "";

	/**
	 * Constructor.
	 * 
	 * @param properties
	 *            the property values
	 */
	public EditProperties(Properties properties) {
		super((Frame) null, "Edit Properties", true);
		this.properties = properties;
	}

	/**
	 * Constructor.
	 * 
	 * @param properties
	 *            the property
	 * @param propertyFile
	 *            the property file
	 */
	public EditProperties(Properties properties, String propertyFile) {
		super((Frame) null, "Edit Properties", true);
		this.propertyFile = propertyFile;
		this.properties = properties;
	}

	/**
	 * Initialize the dialog.
	 */
	public Properties init() {
		Container panel = getContentPane();

		JMenu mFile = new JMenu("File");
		JMenuItem miOpen = new JMenuItem("Open Property File");
		miOpen.addActionListener(new ActionAdaptor() {
			public void actionPerformed(ActionEvent e) {
				openPropertyFile();
			}
		});
		JMenuItem miSave = new JMenuItem("Save Property File");
		miSave.addActionListener(new ActionAdaptor() {
			public void actionPerformed(ActionEvent e) {
				savePropertyFile();
			}
		});
		JMenuItem miQuit = new JMenuItem("Quit");
		miQuit.addActionListener(new ActionAdaptor() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		mFile.add(miOpen);
		mFile.add(miSave);
		mFile.add(miQuit);
		menuBar.add(mFile);

		JMenu mEdit = new JMenu("Edit");
		JMenuItem miAddProperty = new JMenuItem("Add Property");
		miAddProperty.addActionListener(new ActionAdaptor() {
			public void actionPerformed(ActionEvent e) {
				addProperty();
			}
		});
		mEdit.add(miAddProperty);
		menuBar.add(mEdit);
		this.setJMenuBar(menuBar);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setTable();

		setAlwaysOnTop(true);
		scroll.setViewportView(table);
		panel.add(scroll);

		save.addActionListener(new ActionAdaptor() {
			public void actionPerformed(ActionEvent e) {
				saveProperties();
			}
		});
		cancel.addActionListener(new ActionAdaptor() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JPanel control = new JPanel();
		control.setLayout(new FlowLayout());
		control.add(save);
		control.add(cancel);
		panel.add(control, BorderLayout.SOUTH);

		pack();
		setVisible(true);
		
		return properties;
	}

	/**
	 * Prompts for a new property key value.
	 */
	public void addProperty() {
		String key = JOptionPane.showInputDialog(null, "Property");
		properties.setProperty(key, "");
		setTable();
	}

	/**
	 * 
	 * 
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	public String defaultProperty(String propertyName, String defaultValue) {
		String property = "";
		if ((property = properties.getProperty(propertyName)) == null || property.equals(""))
			property = defaultValue;
		properties.setProperty(propertyName, property);
		return property;
	}

	/**
	 * Update the property-value table.
	 */
	public void setTable() {
		DefaultTableModel tableModel = new DefaultTableModel(header, 0);

		for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
			ArrayList<Object> array = new ArrayList<>();
			String prop = (String) e.nextElement();
			array.add(prop);
			array.add(properties.getProperty(prop));
			tableModel.addRow(array.toArray());
		}

		table.setModel(tableModel);

		TableColumnModel tcm = table.getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			TableColumn tc = tcm.getColumn(i);
			tc.setCellRenderer(cellRenderer);
		}
	}

	/**
	 * Prompts for a property file for editing.
	 */
	public void openPropertyFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int res = fileChooser.showOpenDialog(this);
		if (res == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			String fn = file.getPath();
			readProperties(fn);
			setTable();
		}
	}

	/**
	 * Reads property values from the property file.
	 * 
	 * @param fileName
	 *            the property file
	 */
	public void readProperties(String fileName) {
		FileInputStream in = null;
		try {
			File file = new File(fileName);
			if (file.exists() && file.length() > 0) {
				in = new FileInputStream(file);
				properties.loadFromXML(in);
			}
			this.setTitle(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Prompts for property file for saving the property values.
	 */
	public void savePropertyFile() {
		if (propertyFile.isEmpty()) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int res = fileChooser.showSaveDialog(this);
			if (res == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				propertyFile= file.getPath();
			}
		}
		if(!propertyFile.isEmpty()){
			File file=new File(propertyFile);
			if(file.exists() && !file.isDirectory()){
				saveProperties();
				writeProperties(propertyFile);							
			}
		}
	}

	/**
	 * Retrieves the property values from the table.
	 */
	public void saveProperties() {
		String propertyName = "", propertyValue;
		TableCellEditor editor = null;
		if (table != null) {
			editor = table.getCellEditor();
			if (editor != null)
				editor.stopCellEditing();
			Object object = null;
			for (int i = 0; i < table.getRowCount(); i++) {
				object = table.getValueAt(i, 0);
				if (object != null)
					propertyName = (String) object;
				object = table.getValueAt(i, 1);
				propertyValue = (String) object;
				if (!propertyName.equals("") && propertyValue != null) {
					properties.setProperty(propertyName, propertyValue);
				}
			}
		}
	}

	/**
	 * Saves the property values.
	 * 
	 * @param fileName
	 */
	public void writeProperties(String fileName) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
			properties.storeToXML(out, dateFormat.format(new java.util.Date()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// for debugging purposes only
	public static void main(String args[]) {
		Properties properties = new Properties();
		properties.setProperty("application name", "EditProperties");
		properties.setProperty("author", "easai");
		properties.setProperty("date created", "Fri Dec 10 20:50:33 2004");
		properties.setProperty("debug", "true");
		properties.setProperty("verbose", "false");
		EditProperties editProperties = new EditProperties(properties);
		properties=editProperties.init();
	}
}
