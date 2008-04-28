package model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import model.ImageFeatureExtractor.ImageFeatureExtractor;
import model.antipole.AntipoleTree;
import model.antipole.AntipoleTreeImageObject;
import model.antipole.AntipoleTreeObject;

/**
 * This class represents an photomosaic image.
 */
public class Photomosaic extends ImageMatrix implements Runnable {

	/*
	 * The amount of noise that is added for getting more heterogeneus
	 * photomosaics
	 */
	private static final int NOISE_COUNT = 20;

	private AntipoleTree antipoleTree;
	private int imagesDim;
	private ImagesDB imagesDB;
	private ImageFeatureExtractor featureExtractor;
	private PhotomosaicObserver observer;
	AntipoleTreeObject tilesRef[][];

	public Photomosaic(Image image, AntipoleTree antipoleTree, int imagesDim,
			ImagesDB imagesDB, ImageFeatureExtractor featureExtractor,
			PhotomosaicObserver observer) {
		super(image);
		this.antipoleTree = antipoleTree;
		this.imagesDim = imagesDim;
		this.imagesDB = imagesDB;
		this.featureExtractor = featureExtractor;
		this.observer = observer;

		this.tilesRef = new AntipoleTreeObject[height / imagesDim][width
				/ imagesDim];
	}

	@Override
	public void run() {
		int j, i, k, width, height, sign;
		AntipoleTreeObject nearest = null;

		int mean[] = null;
		Random r = new Random();

		width = getWidth();
		height = getHeight();

		/* Select the "best" image for each tile */
		try {
			for (i = 0; i < height && i + imagesDim < height + 1; i += imagesDim) {
				for (j = 0; j < width && j + imagesDim < width + 1; j += imagesDim) {
					/* Compute the RGB mean for the current image portion */
					try {
						mean = featureExtractor.extractFeature(this, j, i,
								imagesDim, imagesDim);
					} catch (InvalidImageDimentionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					/* Introduce a bit of noise */
					for (k = 0; k < mean.length; k++) {
						sign = 2 * r.nextInt(1) - 1;
						mean[k] += (r.nextInt(NOISE_COUNT) * sign);
					}

					/* Get the "nearest" tile */
					nearest = antipoleTree.getNearestObject(
							new AntipoleTreeImageObject(mean), getBoundObjects(
									tilesRef, i, j, imagesDim));

					/* Register the selected tile */
					tilesRef[i / imagesDim][j / imagesDim] = nearest;

					/* Draw the selected tile */
					observer.observe();
				}
			}
		} catch (OutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a collection with the bound MetricSpaceObjects for the current
	 * position.
	 * 
	 * @param tiles
	 *            Matrix with the MetricSpaceObjects.
	 * @param i
	 *            current left position.
	 * @param j
	 *            current top position.
	 * @param dim
	 *            Tiles dimension.
	 */
	private Collection<AntipoleTreeObject> getBoundObjects(
			AntipoleTreeObject tiles[][], int i, int j, int dim) {
		Collection<AntipoleTreeObject> ret = new ArrayList<AntipoleTreeObject>();

		int count = 0;

		if (i != 0) {
			ret.add(tiles[(i - 1) / dim][j / dim]);
			count++;
		}

		if (j != 0) {
			ret.add(tiles[i / dim][(j - 1) / dim]);
			count++;
		}

		if (count == 2)
			ret.add(tiles[(i - 1) / dim][(j - 1) / dim]);

		return ret;
	}

	public AntipoleTreeObject getAntipoleTreeObject(int i, int j) {
		return tilesRef[i / imagesDim][j / imagesDim];
	}

}
