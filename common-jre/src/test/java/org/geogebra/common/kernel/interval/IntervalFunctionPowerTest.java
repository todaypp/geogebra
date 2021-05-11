package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class IntervalFunctionPowerTest extends BaseUnitTest {
	@Test
	public void evaluateXPowerHalf() throws Exception {
		assertEquals(
				interval(1, 16).nthRoot(2),
				evalOnInterval("x^(1/2)", 1, 16));
	}

	private Interval evalOnInterval(String definition, double low, double high) throws Exception {
		GeoFunction geo = add(definition);
		return (new IntervalFunction(geo)).evaluate(interval(low, high));
	}

	@Test
	public void evaluateXPowerForth() throws Exception {
		assertEquals(interval(1, 16).nthRoot(4),
				evalOnInterval("x^(1/4)", 1, 16) );
	}

	@Test
	public void evaluateXPowerTwoThird() throws Exception {
		assertEquals(interval(1, 16).pow(2).nthRoot(3),
				evalOnInterval("x^(2/3)", 1, 16));
	}

	@Test
	public void evaluateXOnNegativeFractionPower() throws Exception {
		assertEquals(interval(9, 10).pow(3).sqrt().multiplicativeInverse(),
				evalOnInterval("x^(-3/2)", 9, 10));
	}

	@Test
	public void evaluateXOnDoublePower() throws Exception {
		assertEquals(interval(9, 10).sqrt(),
				evalOnInterval("x^0.5", 9, 10));
	}

	@Test
	public void evaluatePowerOfNegativeFraction() throws Exception {
		String definition = "x^-(2/9)";
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				evalOnInterval(definition, -6, -5.88));
	}

	private void shouldBeUndefinedAtZero(IntervalFunction function) throws Exception {
		assertEquals(IntervalConstants.undefined(), function.evaluate(IntervalConstants.zero()));
	}

	@Test
	public void evaluatePowerOfFractionNegativeNominator() throws Exception {
		GeoFunction geo = add("x^(-2/9)");
		IntervalFunction function = new IntervalFunction(geo);
		shouldBeUndefinedAtZero(function);
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				function.evaluate(interval(-6, -5.88)));
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				function.evaluate(interval(5.88, 6)));
	}

	@Test
	public void evaluatePowerOfNegativeFractionDenominator() throws Exception {
		GeoFunction geo = add("x^(2/-9)");
		IntervalFunction function = new IntervalFunction(geo);
		shouldBeUndefinedAtZero(function);
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				function.evaluate(interval(-6, -5.88)));
		assertEquals(interval(0.6715486801956773, 0.6745703694731457),
				function.evaluate(interval(5.88, 6)));
	}

}
