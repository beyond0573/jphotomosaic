package model.antipole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class AntipoleTreeBuilder {

	/* TODO: en el articulo hay algunos valores arbitrarios */
	private static final int ONE_MEDIAN_SETS_MAX_SIZE = 100;

	/**
	 * Construye el Antipole Tree.
	 * 
	 * @param objects
	 *            Collección de AntipoleTreeObject que se desean insertar en el
	 *            Antipole Tree.
	 * @param maxRadius
	 *            Radio Máximo de los clusters.
	 * @param antipolePair
	 *            Polos iniciales.
	 * @return Antipole Tree generado.
	 */
	public static AntipoleTree build(Collection<AntipoleTreeObject> objects,
			double maxRadius, Pair<AntipoleTreeObject> antipolePair) {

		// System.out.println(objects.size());
		/* Verificar si se debe o no computar los polos */
		if (antipolePair == null) {
			/* Aproximar los polos (objetos más distantes) */
			antipolePair = getPossibleAntipolePair(objects, maxRadius);

			/*
			 * Si no se generó el par de polos, la colleccion debe convertirse
			 * en un cluster
			 */
			if (antipolePair == null) {
				AntipoleTreeObject centroid = approximate1Median(objects);
				objects.remove(centroid);
				return new AntipoleTree.Cluster(centroid, objects);
			}
		}
		/* Polos ya calculados */

		/* Remover los polos del conjunto de objetos */
		objects.remove(antipolePair.getA());
		objects.remove(antipolePair.getB());

		Collection<AntipoleTreeObject> collectionA = new ArrayList<AntipoleTreeObject>();
		Collection<AntipoleTreeObject> collectionB = new ArrayList<AntipoleTreeObject>();
		double maxDistA = 0, maxDistB = 0, distA, distB;

		/* Poner los objetos en el conjunto que le quede más cerca */
		for (AntipoleTreeObject o : objects) {
			distA = o.distanceTo(antipolePair.getA());
			distB = o.distanceTo(antipolePair.getB());
			if (distA < distB) {
				o.setDistance(distA);
				collectionA.add(o);
				if (distA > maxDistA)
					maxDistA = distA;
			} else {
				o.setDistance(distB);
				collectionB.add(o);
				if (distB > maxDistB)
					maxDistB = distB;
			}
		}

		/* Computar próximos polos */
		Pair<AntipoleTreeObject> antipolePairA = approximateAntipolePair(
				collectionA, maxRadius);
		Pair<AntipoleTreeObject> antipolePairB = approximateAntipolePair(
				collectionB, maxRadius);

		return new AntipoleTree.InternalNode(antipolePair.getA(), antipolePair
				.getB(), maxDistA, maxDistB, build(collectionA, maxRadius,
				antipolePairA), build(collectionB, maxRadius, antipolePairB));
	}

	/* TODO: hay una implementacion aproximada: seccion 3.3 */
	private static Pair<AntipoleTreeObject> approximateAntipolePair(
			Collection<AntipoleTreeObject> collection, double maxRadius) {

		for (AntipoleTreeObject i : collection)
			for (AntipoleTreeObject j : collection)
				if (i != j
						&& Math.abs(i.getDistance() - j.getDistance()) > maxRadius)
					return new Pair<AntipoleTreeObject>(i, j);

		return null;
	}

	private static Pair<AntipoleTreeObject> getPossibleAntipolePair(
			Collection<AntipoleTreeObject> objects, double maxRadius) {

		for (AntipoleTreeObject i : objects)
			for (AntipoleTreeObject j : objects)
				if (i != j && i.distanceTo(j) > maxRadius)
					return new Pair<AntipoleTreeObject>(i, j);

		return null;
	}

	private static AntipoleTreeObject approximate1Median(
			Collection<AntipoleTreeObject> collection) {
		List<ArrayList<AntipoleTreeObject>> sets = new ArrayList<ArrayList<AntipoleTreeObject>>();
		Collection<AntipoleTreeObject> aux;
		Collection<AntipoleTreeObject> newCollection = new ArrayList<AntipoleTreeObject>();
		boolean inserted;

		/* Cut condition */
		if (collection.size() < ONE_MEDIAN_SETS_MAX_SIZE) {
			/* Compute the exact 1-Median! */
			return winner(collection);
		} else {
			/* Divide the set into N subsets */
			for (int i = 0; i < collection.size() / ONE_MEDIAN_SETS_MAX_SIZE
					+ 1; i++)
				sets.add(new ArrayList<AntipoleTreeObject>());

			Random r = new Random();
			int i;
			for (AntipoleTreeObject o : collection) {
				inserted = false;
				while (!inserted) {
					i = r.nextInt(collection.size() / ONE_MEDIAN_SETS_MAX_SIZE);
					aux = sets.get(i);
					if (aux.size() < ONE_MEDIAN_SETS_MAX_SIZE) {
						aux.add(o);
						inserted = true;
					}
				}
			}

			/*
			 * Compute the exact 1-median for each set and generate the new
			 * collection
			 */
			for (Collection<AntipoleTreeObject> c : sets) {
				newCollection.add(winner(c));
			}

			return approximate1Median(newCollection);

		}

	}

	private static AntipoleTreeObject winner(
			Collection<AntipoleTreeObject> collection) {
		AntipoleTreeObject winnerObject = null;
		double currentDistanceSum, minDistanceSum = Double.MAX_VALUE;

		/* Iterate through all the object and compute the min distance sum */
		for (AntipoleTreeObject i : collection) {
			currentDistanceSum = 0;
			for (AntipoleTreeObject j : collection)
				currentDistanceSum += i.distanceTo(j);
			if (currentDistanceSum < minDistanceSum) {
				minDistanceSum = currentDistanceSum;
				winnerObject = i;
			}
		}
		return winnerObject;
	}

	private static class Pair<T> {
		private T A, B;

		public Pair(T a, T b) {
			A = a;
			B = b;
		}

		public T getA() {
			return A;
		}

		public T getB() {
			return B;
		}
	}

}
