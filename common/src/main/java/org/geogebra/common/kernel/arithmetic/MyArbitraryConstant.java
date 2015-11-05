package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.Map;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Arbitrary constant comming from native CAS
 * 
 * Each scope (cas cell or CAS using algo) should have an own instance of this
 * class
 */
public class MyArbitraryConstant {
	/** arbitrary integer */
	public static final int ARB_INT = 0;
	/** arbitrary double */
	public static final int ARB_CONST = 1;
	/** arbitrary complex number */
	public static final int ARB_COMPLEX = 2;

	private ArrayList<GeoNumeric> consts = new ArrayList<GeoNumeric>(),
			ints = new ArrayList<GeoNumeric>(),
			complexNumbers = new ArrayList<GeoNumeric>();

	private ConstructionElement ce;

	/**
	 * Creates new arbitrary constant handler
	 * 
	 * @param ce
	 *            associated construction element
	 */
	public MyArbitraryConstant(ConstructionElement ce) {
		this.ce = ce;
	}

	/*
	 * public static String latexStr(String prefix,Map<Integer,String>
	 * map,Integer number,Construction cons){ String s = map.get(number);
	 * if(s!=null) return s; s = cons.getIndexLabel(prefix, number);
	 * map.put(number, s); return s; }
	 */
	private int position = 0;

	/**
	 * @param myDouble
	 *            constant index (global)
	 * @return real constant
	 */
	public ExpressionValue nextConst(double myDouble) {
		return nextConst(consts, ce.getConstruction().constsM, "c", myDouble);
	}

	/**
	 * @param myDouble
	 *            constant index (global)
	 * @return integer constant
	 */
	public ExpressionValue nextInt(double myDouble) {
		return nextConst(ints, ce.getConstruction().intsM, "k", myDouble);
	}

	/**
	 * @param myDouble
	 *            constant index (global)
	 * @return complex constant
	 */
	public ExpressionValue nextComplex(double myDouble) {
		return nextConst(complexNumbers, ce.getConstruction().complexNumbersM,
				"c", myDouble);
	}

	/**
	 * Returns a number for this scope that corresponds to given native index
	 * 
	 * @param consts2
	 *            all constants of given type (integer / real / complex) in this
	 *            scope cached from last computation
	 * 
	 * @param map
	 *            maps geo labels to constants in the whole construction
	 * @param prefix
	 *            prefix fro this constant type: c for real / complex, k for
	 *            integer
	 * @param index
	 *            index we got from native CAS, we assume it's getting bigger
	 *            with each computation but two constants with the same name
	 *            always refer to the same number eg
	 *            {x+arbonst(10),2x+arbconst(10)+arbconst(9)}
	 * @return element of consts2; if one with a given index already exists in
	 *         map take that one, otherwise pick the next one from consts2 (or
	 *         create one if there are not enough)
	 */
	protected GeoNumeric nextConst(ArrayList<GeoNumeric> consts2,
			Map<Integer, GeoNumeric> map, String prefix, double index) {
		Integer indexInt = new Integer((int) Math.round(index));
		GeoNumeric found = map.get(indexInt);
		if (found != null)
			return found;
		Construction c = ce.getConstruction();
		if (position >= consts2.size() || consts2.get(position) == null) {
			GeoNumeric add = new GeoNumeric(c);
			add.setSendValueToCas(false);
			add.setAuxiliaryObject(true);
			boolean oldLabeling = c.isSuppressLabelsActive();
			c.setSuppressLabelCreation(false);
			add.setLabel(c.getIndexLabel(prefix));
			c.setSuppressLabelCreation(oldLabeling);
			AlgoDependentArbconst algo = new AlgoDependentArbconst(c, add, ce);
			c.removeFromConstructionList(algo);
			consts2.add(position, add);
			position++;
			map.put(indexInt, add);
			return add;
		}
		GeoNumeric ret = consts2.get(position);
		map.put(indexInt, ret);
		position++;
		return ret;
	}

	/**
	 * Resets the handler; must be called before the first next*() call in each
	 * update of the CAS algo that is creating arbconsts
	 */
	public void reset() {
		position = 0;
	}

	/**
	 * Gets arbconst
	 * 
	 * @param i
	 *            index of arbconst within this handler
	 * @return arbconst
	 */
	public GeoNumeric getConst(int i) {
		return consts.get(i);
	}

	/**
	 * @return number of arbconsts, arbcomplexes and arbints together
	 */
	public int getTotalNumberOfConsts() {
		return consts.size() + ints.size() + complexNumbers.size();
	}

	/**
	 * Ensures that update of the constant (if visualised as slider) triggers
	 * update of resulting geo. This is not meant to be contained in
	 * construction protocol.
	 *
	 */
	public class AlgoDependentArbconst extends AlgoElement {
		private GeoElement constant;
		private ConstructionElement outCE;

		// private ArrayList<AlgoElement> updateList;
		/**
		 * @param c
		 *            construction
		 * @param constant
		 *            the constant as a (complex) number
		 * @param outCE
		 *            element that needs updating if the constant changes
		 */
		public AlgoDependentArbconst(Construction c, GeoElement constant,
				ConstructionElement outCE) {
			super(c, false);
			this.constant = constant;
			this.outCE = outCE;
			/**
			 * if(outCE instanceof AlgoElement){ updateList = new
			 * ArrayList<AlgoElement>(); updateList.add((AlgoElement)outCE); }
			 */

			setInputOutput();
		}

		@Override
		protected void setInputOutput() {
			input = new GeoElement[] { constant };
			setDependencies();
		}

		@Override
		public void compute() {
			if (outCE instanceof AlgoElement
					&& ((AlgoElement) outCE).getOutputLength() == 1)
				((AlgoElement) outCE).getOutput(0).updateCascade();
			else if (outCE != null)
				outCE.update();
		}

		@Override
		public Algos getClassName() {
			return Algos.Expression;
		}

	}

	/**
	 * @return whether this handler is bound with CAS cell
	 */
	public boolean isCAS() {
		return ce instanceof GeoCasCell;
	}

}
