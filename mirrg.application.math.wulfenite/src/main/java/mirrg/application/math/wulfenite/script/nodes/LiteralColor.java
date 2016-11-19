package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.SlotColor;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LiteralColor extends ScriptNodeBase
{

	private String string;

	public LiteralColor(Node<?> node, String string)
	{
		super(node);
		this.string = string;
	}

	private SlotColor slot;

	@Override
	public boolean validate(Environment environment)
	{

		if (string.length() == 3) {
			slot = new SlotColor(Integer.parseInt("" +
				string.charAt(0) + string.charAt(0) +
				string.charAt(1) + string.charAt(1) +
				string.charAt(2) + string.charAt(2), 16));
			return true;
		}

		if (string.length() == 6) {
			slot = new SlotColor(Integer.parseInt(string, 16));
			return true;
		}

		environment.reportError("Illegal color expression: " + string, this);
		return false;
	}

	@Override
	public Class<?> getType()
	{
		return SlotColor.class;
	}

	@Override
	public Object getValue()
	{
		return slot;
	}

}
