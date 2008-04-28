package model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import model.ImageFeatureExtractor.ImageFeatureExtractor;
import model.antipole.AntipoleTreeImageObject;
import model.antipole.AntipoleTreeObject;

/**
 * This class represents the image tiles database. The DB is divided into 2
 * files: a compressed one that contains the images and another wich contains
 * references to them with aditional information about each one. Because this
 * information doesn't depends on the photomosaic image, it's not necesary to
 * calculate each time a photomosaic is make, so for efficiency convenience,
 * this data is persisted.
 */
public class ImagesDB {

	/* The compressed file that contains the tiles images */
	private String imageFile;
	
	private int width = 30; // TODO: levantar de archivo
	private int height = 30;

	/*
	 * The file that contains the tiles aditional information needed to build
	 * the index
	 */
	private String dataFile;

	/* Cache for Image objects */
	private Hashtable<String, ImageMatrix> cache;

	private final static String DATA_FILE_SEPARATOR = " \t";

	/**
	 * This constructor is used when the image data has already been calculated.
	 * 
	 * @param imageFile
	 *            The compressed file that contains the image tiles.
	 * @param dataFile
	 *            The file where the extra tile information is.
	 */
	public ImagesDB(String imageFile, String dataFile)
			throws FileNotFoundException {
		if (!(new File(imageFile).exists()) || !(new File(dataFile).exists()))
			throw new FileNotFoundException();

		this.imageFile = imageFile;
		this.dataFile = dataFile;

		cache = new Hashtable<String, ImageMatrix>();
	}

	/**
	 * Generate the dataFile file.
	 * 
	 * @param imageFile
	 *            The compressed file that contains the image tiles.
	 * @param dataFile
	 *            The output file where the extra tile information will be
	 *            placed.
	 */
	public static void processTilesFile(String imageFile, String dataFile,
			ImageFeatureExtractor featureExtractor)
			throws FileNotFoundException, IOException {
		ZipFile zf = null;
		ZipEntry entry;
		InputStream stream;
		ImageMatrix image;
		BufferedWriter bw = null;
		int data[] = null;

		if (!(new File(imageFile).exists()))
			throw new FileNotFoundException();

		try {
			zf = new ZipFile(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		bw = new BufferedWriter(new FileWriter(dataFile));

		/* Iterate trough all the entries */
		for (Enumeration<? extends ZipEntry> iter = zf.entries(); iter
				.hasMoreElements();) {
			/* Process the current image */
			entry = (ZipEntry) iter.nextElement();
			if (!entry.isDirectory()) {
				stream = zf.getInputStream(entry);
				image = new ImageMatrix(ImageIO.read(stream));
				bw.write(entry.getName());
				bw.write("\n");

				try {
					data = featureExtractor.extractFeature(image, 0, 0, image
							.getWidth(), image.getWidth());
				} catch (OutOfBoundsException e) {
					/* Should never happen */
					e.printStackTrace();
				} catch (InvalidImageDimentionException e) {
					/* Should never happen */
					e.printStackTrace();
				}
				for (int i = 0; i < data.length; i++) {
					bw.write(new Integer(data[i]).toString());
					bw.write(" ");
				}
				bw.write("\n");
			}
		}
		bw.close();
	}

	/**
	 * Retrieves a collection with all the image tiles arranged into Object
	 * classes for the Antipole Tree convenience.
	 * 
	 * @return collection of Object objects
	 */
	public Collection<AntipoleTreeObject> getAllObjects() {
		Collection<AntipoleTreeObject> ret = new ArrayList<AntipoleTreeObject>();
		BufferedReader f = null;
		String name, data;
		int feature[];
		StringTokenizer st;
		int i;

		try {
			f = new BufferedReader(new FileReader(dataFile));
		} catch (FileNotFoundException e1) {
			/* Never should happen */
			e1.printStackTrace();
		}

		try {
			while ((name = f.readLine()) != null) {
				data = f.readLine();
				st = new StringTokenizer(data, DATA_FILE_SEPARATOR);
				feature = new int[27];
				for (i = 0; i < feature.length; i++) {
					feature[i] = Integer.valueOf(st.nextToken()).intValue();
				}

				ret.add(new AntipoleTreeImageObject(name, feature));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * Return the Image object for the specified image name
	 * 
	 * @param name
	 *            The name of the image to be retireved
	 * @return The image object
	 * @throws IOException
	 */
	public ImageMatrix getImage(String name) throws IOException {
		ImageMatrix ret;
		ZipFile zf;

		/* Search for the key in the cache */
		ret = cache.get(name);
		if (ret == null) {
			/* Load the image into memory */
			zf = new ZipFile(imageFile);
			ret = new ImageMatrix(ImageIO.read(zf.getInputStream(zf
					.getEntry(name))));
			cache.put(name, ret);
		}

		return ret;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
}