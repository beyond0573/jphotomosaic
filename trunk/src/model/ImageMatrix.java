package model;

import java.awt.Color;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageMatrix {

	private Image image;
	protected int[][] pixels;
	protected int width;
	protected int height;

	public ImageMatrix(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[height][width];
	}
	
	public ImageMatrix(ImageMatrix other){
		this.image = other.image;
		this.width = other.width;
		this.height = other.height;
		this.pixels = new int[this.height][this.width];
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				this.pixels[i][j] = other.pixels[i][j]; 
			}
		}
	}

	public ImageMatrix(Image image) {
		this(image.getWidth(null), image.getHeight(null));
		this.image = image;
		this.pixels = new int[height][width];

		int[] pixelsTemp = new int[this.width * this.height];

		PixelGrabber grabber = new PixelGrabber(image, 0, 0, this.width,
				this.height, pixelsTemp, 0, this.width);
		try {
			grabber.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace(); // TODO
		}

		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				this.pixels[i][j] = pixelsTemp[i * this.width + j];
			}
		}
	}

	public ImageProducer getImageProducer() {
		int[] pixelsTemp = new int[this.width * this.height];

		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				pixelsTemp[i * this.width + j] = this.pixels[i][j];
			}
		}

		return new MemoryImageSource(this.width, this.height, pixelsTemp, 0,
				this.width);
	}
	
	public Image getImage(){
		return image;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int[][] getPixels() {
		return pixels;
	}

	// public Image(InputStream imageStream) throws IOException {
	// image = ImageIO.read(imageStream);
	//
	// pixels = new int[image.height][image.width];
	// int[] rasterPixels = new int[3 * image.width * image.height];
	// rasterPixels = image.getRaster().getPixels(0, 0, image.width,
	// image.height, rasterPixels);
	//
	// /* Construir matriz de pixeles */
	// Color c;
	// for (int i = 0; i < image.height; i++) {
	// for (int j = 0; j < image.width; j++) {
	// c = new Color(rasterPixels[i * 3 * image.width + 3 * j],
	// rasterPixels[i * 3 * image.width + 3 * j + 1],
	// rasterPixels[i * 3 * image.width + 3 * j + 2]);
	// pixels[i][j] = c.getRGB();
	// }
	// }
	// }

	/**
	 * Replace part of this image with another one.
	 * 
	 * @param image
	 *            The image that will replace some part of this one.
	 * @param x
	 *            Horizontal offset.
	 * @param y
	 *            Vertical offset.
	 * @throws OutOfBoundsException
	 *             When the new image doesn fit into the original one.
	 */
	public void DrawInImage(ImageMatrix image, int x, int y)
			throws OutOfBoundsException {
		/* Check wheter the dimensions are valid */
		if (x > width || x + image.width > width || y > height
				|| y + image.height > height)
			throw new OutOfBoundsException();

		/* Replace the indicated part of the original image */

		for (int i = y; i < y + image.height; i++) {
			for (int j = x; j < x + image.width; j++) {
				pixels[i][j] = image.pixels[i - y][j - x];
			}
		}
	}

	public void save(File f) throws IOException{
		BufferedImage bufferedImage = new BufferedImage(width, height,
				ColorSpace.CS_sRGB);

		Color c;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				c = new Color(pixels[i][j]);
				bufferedImage.setRGB(j, i, c.getRGB());
			}
		}

		
		ImageIO.write(bufferedImage, "jpeg", f);
		
	}
}
