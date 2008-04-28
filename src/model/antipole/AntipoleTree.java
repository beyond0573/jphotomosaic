package model.antipole;

import java.util.Collection;

/**
 * Implementación del Antipole Tree.
 */
public abstract class AntipoleTree {

	/**
	 * @param query
	 *            AntipoleTreeObject de la consulta.
	 * @param invalidObjects
	 *            AntipoleTreeObject's inválidos como resultado de la consulta.
	 * @return El AntipoleTreeObject más cercano al de la consulta.
	 */
	public abstract AntipoleTreeObject getNearestObject(
			AntipoleTreeObject query,
			Collection<AntipoleTreeObject> invalidObjects);

	public static class InternalNode extends AntipoleTree {

		/* Polos del nodo */
		private AntipoleTreeObject A, B;

		/* The Antipole pair radius */
		private double radiusA, radiusB;

		/* References to the Antipole pair subtree */
		private AntipoleTree treeA, treeB;

		public InternalNode(AntipoleTreeObject A, AntipoleTreeObject B,
				double radiusA, double radiusB, AntipoleTree treeA,
				AntipoleTree treeB) {
			this.A = A;
			this.B = B;
			this.radiusA = radiusA;
			this.radiusB = radiusB;
			this.treeA = treeA;
			this.treeB = treeB;
		}

		/**
		 * @param query
		 *            AntipoleTreeObject de la consulta.
		 * @param invalidObjects
		 *            AntipoleTreeObject's inválidos como resultado de la
		 *            consulta.
		 * @return El AntipoleTreeObject más cercano al de la consulta.
		 */
		public AntipoleTreeObject getNearestObject(AntipoleTreeObject query,
				Collection<AntipoleTreeObject> invalidObjects) {
			double distA, distB;
			AntipoleTreeObject ret = null;

			distA = query.distanceTo(A);
			if (distA == 0)
				return A;

			distB = query.distanceTo(B);
			if (distA == 0)
				return B;

			if (distA < distB) {
				ret = treeA.getNearestObject(query, invalidObjects);
				return ((ret == null) ? A : ret);
			} else {
				ret = treeB.getNearestObject(query, invalidObjects);
				return ((ret == null) ? B : ret);
			}
		}

	}

	/**
	 * Implementación de los clusters del Antipole Tree. Los clusters son las
	 * hojas del árbol.
	 */
	public static class Cluster extends AntipoleTree {

		/* Centroide */
		private AntipoleTreeObject centroid;

		/*
		 * Radio (distancia desde el centroide al AntipoleTreeObject más lejano
		 * del cluster)
		 */
		private double radius;

		/* Objetos que contiene el cluster */
		private Collection<AntipoleTreeObject> objects;

		public Cluster(AntipoleTreeObject centroid,
				Collection<AntipoleTreeObject> objects) {
			this.centroid = centroid;
			this.objects = objects;

			double maxDistance = -1, distance;
			for (AntipoleTreeObject o : objects) {
				distance = o.distanceTo(centroid);
				if (distance > maxDistance)
					maxDistance = distance;
				o.setDistance(distance);
			}

			this.radius = maxDistance;

		}

		/**
		 * 
		 * @param object
		 *            AntipoleTreeObject de la consulta.
		 * @param invalidObjects
		 *            AntipoleTreeObject's inválidos como resultado de la
		 *            consulta.
		 * @return El AntipoleTreeObject más cercano al de la consulta.
		 */
		public AntipoleTreeObject getNearestObject(AntipoleTreeObject object,
				Collection<AntipoleTreeObject> invalidObjects) {
			double minDistance = Double.MAX_VALUE, distance;
			AntipoleTreeObject ret = null;

			for (AntipoleTreeObject o : objects) {
				if (!invalidObjects.contains(o)) {
					distance = object.distanceTo(o);
					if (distance < minDistance) {
						minDistance = distance;
						ret = o;
					}
				}
			}

			if (ret == null)
				ret = centroid;

			return ret;
		}

	}
}