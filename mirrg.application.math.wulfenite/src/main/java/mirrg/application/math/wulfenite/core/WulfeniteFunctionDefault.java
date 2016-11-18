package mirrg.application.math.wulfenite.core;

import mirrg.helium.math.hydrogen.complex.StructureComplex;

public class WulfeniteFunctionDefault implements IWulfeniteFunction
{

	@Override
	public boolean isValuePresent()
	{
		return false;
	}

	@Override
	public void getValue(StructureComplex buffer)
	{

	}

	@Override
	public int getColor(StructureComplex coordinate)
	{
		return 0xff000000;
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
