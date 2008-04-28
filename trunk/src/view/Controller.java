package view;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import model.ImageMatrix;
import model.ImagesDB;
import model.OutOfBoundsException;
import model.Photomosaic;
import model.PhotomosaicObserver;
import model.ImageFeatureExtractor.RGBImageFeatureExtractor;
import model.antipole.AntipoleTree;
import model.antipole.AntipoleTreeBuilder;
import model.antipole.AntipoleTreeImageObject;
import model.antipole.AntipoleTreeObject;

public class Controller {

	private PhotomosaicMain view;

	private File lastSavedImage;

	private BufferedImage original;

	private double zoom;

	private ImageMatrix matrix;

	private Photomosaic photomosaic;

	private ImagesDB imagesDB;

	private AntipoleTree antipoleTree;

	public Controller(PhotomosaicMain frame) {
		view = frame;
		lastSavedImage = null;
		original = null;
		zoom = 1.0;
		photomosaic = null;

		try {
			imagesDB = new ImagesDB("tiles/tiles.zip", "tiles/tilesRGB.data");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/* Load the antipole tree */
		System.out.println("Building Antipole Tree...");
		antipoleTree = AntipoleTreeBuilder.build(imagesDB.getAllObjects(), 10,
				null);
		System.out
				.println("Finished building Antipole Tree, now the photomosaic will be generated...");

	}

	public void open() {
		File f = view.getFileFromFileChooser();
		if (f != null) {
			ImageView iv = view.getImageView();
			try {
				original = ImageIO.read(f);
				iv.loadNewImage(original);
				matrix = new ImageMatrix(original);
				iv.redrawEditedImage(matrix.getImageProducer());
				lastSavedImage = null;
			} catch (IOException e) {
				view.showErrorDialog("Error de lectura", String.format(
						"No se puede abrir el archivo:\n%s\n\nDetalle: %s", f
								.toString(), e.getLocalizedMessage()));
			}
		}
		
		/* Escalar lo necesario para que se vean en bien en cada panel */
		
	}

	public void save() {
		if (lastSavedImage == null) {
			saveAs();
		} else {
			saveAs(lastSavedImage);
		}
	}

	public void saveAs() {
		if (photomosaic == null) {
			view.showErrorDialog("Error", "Aplique un método"
					+ " de segmentación a la imagen original"
					+ " antes de guardar los resultados");
		}
		saveAs(view.saveFileWithFileChooser());

	}

	public void saveAs(File f) {
		try {
			/* Save the image */
			System.out
					.println("Finished the photomosaic, now image will be saved...");
			photomosaic.save(f);
			System.out.println("Finished saving the image...");
			lastSavedImage = f;
		} catch (IOException e) {
			view.showErrorDialog("Error de escritura", String.format(
					"No se puede guardar el archivo:\n%s\n\nDetalle: %s", f
							.toString(), e.getLocalizedMessage()));
		}
	}

	public void quit() {
		view.quit();
	}

	public void photomosaic() {
		System.out.println("Generando Photomosaic...");

		photomosaic = new Photomosaic(original, antipoleTree, imagesDB
				.getWidth(), imagesDB, new RGBImageFeatureExtractor(),
				new PhotomosaicObserver() {
					private int i = 0, j = 0,
							imagesWidth = imagesDB.getWidth(),
							imagesHeight = imagesDB.getHeight();

					public void observe() {
						AntipoleTreeObject nearest = photomosaic
								.getAntipoleTreeObject(i, j);
						try {
							photomosaic
									.DrawInImage(
											imagesDB
													.getImage(((AntipoleTreeImageObject) nearest)
															.getName()), j, i);
						} catch (OutOfBoundsException e) {
							System.out.println(e.getLocalizedMessage());
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						j += imagesWidth;
						if (j + imagesWidth >= original.getWidth()) {
							j = 0;
							i += imagesHeight;
							view.getImageView().redrawEditedImage(
									photomosaic.getImageProducer());
						}

					}

				});

		System.out.println("Processing image");
		new Thread(photomosaic).start();
	}

	public void viewHorizontal() {
		view.getImageView().setHorizontalView();
	}

	public void viewVertical() {
		view.getImageView().setVerticalView();
	}

	public void viewTabs() {
		System.err.println("Not yet implemented!");
	}

	public void zoomIn() {
		zoom *= 1.5;
		scale(zoom);
	}

	public void zoomOut() {
		zoom *= 0.75;
		scale(zoom);
	}

	public void scale(double factor) {
		if (original == null)
			return;
		double w = original.getWidth() * factor;
		double h = original.getHeight() * factor;
		Image i = original == null ? null : original.getScaledInstance((int) w,
				(int) h, Image.SCALE_SMOOTH);
		Image j = photomosaic.getImage() == null ? null : photomosaic
				.getImage().getScaledInstance((int) w, (int) h,
						Image.SCALE_SMOOTH);
		view.getImageView().redrawImages(i, j);
	}

	public void about() {
		final String s = "JPhotomosaic"; 
		view.showInformationDialog("Acerca del programa", s);
	}

}