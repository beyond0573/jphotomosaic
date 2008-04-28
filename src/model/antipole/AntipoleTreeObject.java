package model.antipole;

/**
 * Esta interfaz la deben inplementar aquellos objetos que quieran ser
 * insertados en un Antipole Tree
 */
public interface AntipoleTreeObject {

	/**
	 * @return Distancia al centroide o al polo más cercano según corresponda.
	 */
	public double getDistance();

	/**
	 * @param distance
	 *            Distancia al centroide o al polo más cercano según
	 *            corresponda.
	 */
	public void setDistance(double distance);

	/**
	 * @param object
	 * @return Distancia entre los 2 objetos.
	 */
	public double distanceTo(AntipoleTreeObject object);

}
