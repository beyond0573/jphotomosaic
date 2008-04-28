package model.antipole;

/**
 * Implementación de AntipoleTreeObject para insertar imágenes en un Antipole
 * Tree. Las imágenes están compuestas por un arreglo que las caracteriza y una
 * referencia a la imagen que representan para al final hacer el dump del
 * photomosaic.
 */
public class AntipoleTreeImageObject implements AntipoleTreeObject {

	/* Referencia a la imagen */
	private String imageRef;

	/* Característica de esta imagen */
	public int feature[];

	/* Distancia al centroide */
	private double distance;

	public AntipoleTreeImageObject(String imageRef, int feature[],
			double distance) {
		this.imageRef = imageRef;
		this.feature = feature;
		this.distance = distance;
	}

	public AntipoleTreeImageObject(String imageRef, int feature[]) {
		this(imageRef, feature, -1);
	}

	public AntipoleTreeImageObject(int[] feature) {
		this(null, feature, -1);
	}

	/**
	 * Distancia Euclideana
	 */
	public double distanceTo(AntipoleTreeObject object) {
		double ret = 0;

		for (int i = 0; i < feature.length; i++)
			ret += Math
					.pow(
							(feature[i] - ((AntipoleTreeImageObject) object).feature[i]),
							2.0);

		return Math.sqrt(ret);
	}

	/**
	 * @return Referencia a la imagen
	 */
	public String getName() {
		return imageRef;
	}

	@Override
	public double getDistance() {
		return distance;
	}

	@Override
	public void setDistance(double distance) {
		this.distance = distance;
	}
}
