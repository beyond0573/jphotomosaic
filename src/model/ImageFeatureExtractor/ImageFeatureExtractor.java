package model.ImageFeatureExtractor;

import model.ImageMatrix;
import model.InvalidImageDimentionException;
import model.OutOfBoundsException;

public interface ImageFeatureExtractor {

	public int[] extractFeature(ImageMatrix image, int x, int y, int width, int height)
			throws InvalidImageDimentionException, OutOfBoundsException;

}
