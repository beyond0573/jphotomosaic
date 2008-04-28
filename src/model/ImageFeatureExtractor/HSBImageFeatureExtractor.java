package model.ImageFeatureExtractor;

import java.awt.Color;


public class HSBImageFeatureExtractor extends ColorImageFeatureExtractor {

	@Override
	protected int[] getComponents(int pixel) {
		int[] components = new int[3];
		float[] hsbvals = new float[3];
		Color c = new Color(pixel);

		hsbvals = Color
				.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbvals);
		components[0] = (int) (hsbvals[0] * 255);
		components[1] = (int) (hsbvals[1] * 255);
		components[2] = (int) (hsbvals[2] * 255);
		return components;

	}

}
