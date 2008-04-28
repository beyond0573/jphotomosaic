package model.ImageFeatureExtractor;

import java.awt.Color;


public class RGBImageFeatureExtractor extends ColorImageFeatureExtractor {
	@Override
	protected int[] getComponents(int pixel) {
		int[] components = new int[3];
		Color c = new Color(pixel);
		
		components[0] = c.getRed();
		components[1] = c.getGreen();
		components[2] = c.getBlue();
		return components;
	}

}
