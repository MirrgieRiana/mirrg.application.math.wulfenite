package mirrg.application.math.wulfenite.core;

import mirrg.helium.swing.nitrogen.util.HColor;

public class WulfeniteFunctionComplex implements IWulfeniteFunction
{

	@Override
	public int getColor(double coordinateX, double coordinateY)
	{
		double[] value = new double[2];
		getValue(value, coordinateX, coordinateY);

		double abs = Math.sqrt(value[0] * value[0] + value[1] * value[1]);
		double arg = Math.atan2(value[1], value[0]);

		return 0xff000000 | getColorIntFromPolar(abs, arg);
	}

	public int getColorIntFromPolar(double abs, double arg)
	{
		double R = Math.cos(arg);
		double G = Math.cos(arg - 120.0 / 180 * Math.PI);
		double B = Math.cos(arg - 240.0 / 180 * Math.PI);

		double brightness = 0.5 - 0.5 * Math.cos(abs * 2 * Math.PI);

		brightness = 1 - Math.pow(brightness, 2);

		return HColor.getColorInt(
			(int) ((128 + (126 * R)) * brightness),
			(int) ((128 + (126 * G)) * brightness),
			(int) ((128 + (126 * B)) * brightness));
	}

	@Override
	public boolean isValuePresent()
	{
		return true;
	}

	@Override
	public void getValue(double[] dest, double coordinateX, double coordinateY)
	{
		dest[0] = 1 * coordinateX;
		dest[1] = 1 * coordinateY;
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
