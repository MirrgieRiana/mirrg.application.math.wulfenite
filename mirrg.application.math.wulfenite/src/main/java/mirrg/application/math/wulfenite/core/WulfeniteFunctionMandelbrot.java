package mirrg.application.math.wulfenite.core;

import mirrg.helium.swing.nitrogen.util.HColor;

public class WulfeniteFunctionMandelbrot implements IWulfeniteFunction
{

	@Override
	public int getColor(double coordinateX, double coordinateY)
	{
		return getMandelbrot(coordinateX, coordinateY);
	}

	private int getMandelbrot(double coordinateX, double coordinateY)
	{
		double[] value = new double[2];
		getValue(value, coordinateX, coordinateY);

		return HColor.createColor(128 - 128 * Math.cos(value[0] * 3.1415 / 90), 0, 0).getRGB(); // TODO
	}

	@Override
	public boolean isValuePresent()
	{
		return true;
	}

	@Override
	public void getValue(double[] dest, double coordinateX, double coordinateY)
	{
		int t = 0;
		double x2 = 0;
		double y2 = 0;
		while (t < 360 * 4) {
			x2 += coordinateX;
			y2 += coordinateY;
			double a = x2 * x2;
			double b = y2 * y2;
			if (a + b > 4) break;
			double tmp = a - b;
			y2 = 2 * x2 * y2;
			x2 = tmp;
			t++;
		}

		dest[0] = t;
		dest[1] = 0;
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
