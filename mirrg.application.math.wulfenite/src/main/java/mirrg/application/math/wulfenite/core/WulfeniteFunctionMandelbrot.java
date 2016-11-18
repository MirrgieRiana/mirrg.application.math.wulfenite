package mirrg.application.math.wulfenite.core;

import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.nitrogen.util.HColor;

public class WulfeniteFunctionMandelbrot extends WulfeniteFunctionBase
{

	public WulfeniteFunctionMandelbrot(Wulfenite wulfenite)
	{
		super(wulfenite);
	}

	@Override
	public int getColor(StructureComplex coordinate)
	{
		return getMandelbrot(coordinate);
	}

	private int getMandelbrot(StructureComplex coordinate)
	{
		double[] value = new double[2];
		getValue(coordinate);

		return HColor.createColor(128 - 128 * Math.cos(value[0] * 3.1415 / 90), 0, 0).getRGB(); // TODO
	}

	@Override
	public boolean isValuePresent()
	{
		return true;
	}

	@Override
	public void getValue(StructureComplex buffer)
	{
		int t = 0;
		double x2 = 0;
		double y2 = 0;
		while (t < 360 * 4) {
			x2 += buffer.re;
			y2 += buffer.im;
			double a = x2 * x2;
			double b = y2 * y2;
			if (a + b > 4) break;
			double tmp = a - b;
			y2 = 2 * x2 * y2;
			x2 = tmp;
			t++;
		}

		buffer.re = t;
		buffer.im = 0;
	}

	@Override
	public void toggleDialog()
	{

	}

	@Override
	public void dispose()
	{

	}

}
