package mirrg.application.math.wulfenite.core;

public interface IWulfeniteFunction
{

	public int getColor(double coordinateX, double coordinateY);

	public boolean isValuePresent();

	public void getValue(double[] dest, double coordinateX, double coordinateY);

	public void toggleDialog();

	public void dispose();

}
