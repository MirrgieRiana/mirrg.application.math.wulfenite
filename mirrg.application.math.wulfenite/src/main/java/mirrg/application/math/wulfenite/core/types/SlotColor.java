package mirrg.application.math.wulfenite.core.types;

public class SlotColor
{

	public int value;

	public SlotColor()
	{

	}

	public SlotColor(int value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return String.format("#%06x", value & 0x00ffffff);
	}

}
