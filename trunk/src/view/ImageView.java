package view;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;

import javax.swing.*;

public class ImageView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JScrollPane pane1;

	private JScrollPane pane2;

	public ImageView() {
		super(new GridLayout(1, 2));
		pane1 = addScrollPane("Imagen original");
		pane2 = addScrollPane("Imagen segmentada");
	}

	private JScrollPane addScrollPane(String s) {
		JScrollPane pane = new JScrollPane(getScrollPaneLabel(s));
		pane.setVisible(true);
		add(pane);
		return pane;
	}

	private JLabel getScrollPaneLabel(String s) {
		JLabel label = new JLabel(s);
		label.setForeground(new Color(128, 128, 128));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	public void setHorizontalView() {
		setLayout(new GridLayout(1, 2));
		validate();
	}

	public void setVerticalView() {
		setLayout(new GridLayout(2, 1));
		validate();
	}

	public void setNotebookView() {
		System.err.println("Not yet implemented!");
	}

	public void loadNewImage(BufferedImage image) throws IOException {
		redrawPane(pane1, image);
		redrawPane(pane2, image);
	}
	
	public Image produceImage(ImageProducer p) {
		return getToolkit().createImage(p);
	}
	
	public void redrawImages(Image original, Image segmented) {
		redrawPane(pane1, original);
		redrawPane(pane2, segmented);
	}

	public void redrawEditedImage(ImageProducer p) {
		redrawPane(pane2, produceImage(p));
	}

	private void redrawPane(JScrollPane pane, Image image) {
		JLabel label = null;
		if (image == null) {
			label = getScrollPaneLabel(pane == pane1 ? "Imagen original"
					: "Imagen segmentada"); // TODO: no repetir el codigo del constructor
		} else {
			label = new JLabel(new ImageIcon(image));
		}
		label.setVisible(true);
		pane.setViewportView(label);
		label.validate();
	}
}