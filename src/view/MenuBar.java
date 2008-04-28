package view;

import javax.swing.*;

public class MenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Controller ctrl;
	
	public MenuBar(Controller controller) {
		super();
		ctrl = controller;
		add(getFileMenu());
		add(getEditMenu());
		add(getViewMenu());
		add(getHelpMenu());
	}
	
	public JMenu getFileMenu() {
		JMenu menu = new JMenu("Archivo");
		menu.add(getMenuItem("Abrir...", "open"));
		menu.add(getMenuItem("Guardar", "save"));
		menu.add(getMenuItem("Guardar como...", "saveAs"));
		menu.add(getMenuItem("Salir", "quit"));
		return menu;
	}
	
	public JMenu getEditMenu() {
		JMenu menu = new JMenu("Edición");
		menu.add(getMenuItem("Generar Foto-Mosaico", "photomosaic"));
		return menu;
	}
	
	public JMenu getViewMenu() {
		JMenu menu = new JMenu("Ver");
		menu.add(getMenuItem("Acercar", "zoomIn"));
		menu.add(getMenuItem("Alejar", "zoomOut"));
		menu.add(getMenuItem("En horizontal", "viewHorizontal"));
		menu.add(getMenuItem("En vertical", "viewVertical"));
		menu.add(getMenuItem("En pestañas", "viewTabs"));
		return menu;
	}
	
	public JMenu getHelpMenu() {
		JMenu menu = new JMenu("Ayuda");
		menu.add(getMenuItem("Acerca de...", "about"));
		return menu;
	}
	
	public JMenuItem getMenuItem(String label, String callback) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(new ActionListener(ctrl, callback));
		return item;
	}
}
