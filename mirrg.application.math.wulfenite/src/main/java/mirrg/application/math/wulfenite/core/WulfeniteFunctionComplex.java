package mirrg.application.math.wulfenite.core;

import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.nitrogen.util.HColor;

public class WulfeniteFunctionComplex extends WulfeniteFunctionBase
{

	public WulfeniteFunctionComplex(Wulfenite wulfenite)
	{
		super(wulfenite);
	}

	@Override
	public int getColor(StructureComplex coordinate)
	{
		getValue(coordinate);
		return 0xff000000 | getColorIntFromComplex(coordinate);
	}

	@Override
	public boolean isValuePresent()
	{
		return true;
	}

	@Override
	public void getValue(StructureComplex buffer)
	{
		buffer.re = 1 * buffer.re;
		buffer.im = 1 * buffer.im;
	}

	@Override
	public void toggleDialog()
	{

	}

	@Override
	public void dispose()
	{

	}

	public static int getColorIntFromComplex(StructureComplex coordinate)
	{
		return getColorIntFromPolar(coordinate.getAbstract(), coordinate.getArgument());
	}

	public static int getColorIntFromPolar(double abs, double arg)
	{
		double R = Math.cos(arg);
		double G = Math.cos(arg - 120.0 / 180 * Math.PI);
		double B = Math.cos(arg - 240.0 / 180 * Math.PI);

		double brightness = 0.5 - 0.5 * Math.cos(abs * 2 * Math.PI);

		brightness = 1 - brightness * brightness;

		return HColor.getColorInt(
			(int) ((128 + (126 * R)) * brightness),
			(int) ((128 + (126 * G)) * brightness),
			(int) ((128 + (126 * B)) * brightness));
	}

}
