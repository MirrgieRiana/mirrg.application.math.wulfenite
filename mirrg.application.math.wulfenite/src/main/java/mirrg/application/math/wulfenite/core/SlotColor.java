package mirrg.application.math.wulfenite.core;

public class SlotColor
{

	public int rgb;

	public SlotColor()
	{

	}

	public SlotColor(int rgb)
	{
		this.rgb = rgb;
	}

	@Override
	public String toString()
	{
		return String.format("#%06x", rgb & 0x00ffffff);
	}

}
