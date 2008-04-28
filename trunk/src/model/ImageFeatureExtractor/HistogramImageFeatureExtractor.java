package model.ImageFeatureExtractor;

import model.ImageMatrix;
import model.InvalidImageDimentionException;
import model.OutOfBoundsException;

//TODO
public abstract class HistogramImageFeatureExtractor implements ImageFeatureExtractor {

	@Override
	public int[] extractFeature(ImageMatrix image, int x, int y, int width, int height)
			throws InvalidImageDimentionException, OutOfBoundsException {
		int dim = width;
		int sum[] = new int[27];
		int ret[] = new int[27];
		int w, h, i, j, pixelsPerSquare, squareDim;
		int[][] pixels = image.getPixels();

		/* Check that the specified size is divisible by 3 */
		if ((dim - x) % 3 != 0 || (dim - y) % 3 != 0)
			throw new InvalidImageDimentionException();

		/* Read image information */
		w = image.getWidth();
		h = image.getHeight();

		squareDim = dim / 3;
		pixelsPerSquare = squareDim * squareDim;

		/* Check for valid parameters */
		if (x > w || y > h || x + dim > w || y + dim > h)
			throw new OutOfBoundsException();

		/* Compute the RGB sum for the 3x3 division */
		int[] components;
		int l, m;
		for (i = y; i < y + dim; i++) {
			for (j = x; j < x + dim; j++) {
				components = getComponents(pixels[i][j]);
				l = i - y;
				m = j - x;
				sum[l / squareDim * 3 + m / squareDim] += components[0];
				sum[9 + l / squareDim * 3 + m / squareDim] += components[1];
				sum[18 + l / squareDim * 3 + m / squareDim] += components[2];
			}
		}

		/* Computes the mean */
		for (i = 0; i < ret.length; i++) {
			ret[i] = sum[i] / pixelsPerSquare;
		}

		return ret;
	}
	
	protected abstract int[] getComponents(int pixel);

}
