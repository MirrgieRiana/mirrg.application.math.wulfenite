package mirrg.application.math.wulfenite.core;

public class WulfeniteFunctionDefault implements IWulfeniteFunction
{

	@Override
	public boolean isValuePresent()
	{
		return false;
	}

	@Override
	public void getValue(double[] dest, double coordinateX, double coordinateY)
	{

	}

	@Override
	public int getColor(double coordinateX, double coordinateY)
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
