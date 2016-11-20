package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.types.SlotInteger;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LiteralInteger extends ScriptNodeBase
{

	private String string;

	public LiteralInteger(Node<?> node, String string)
	{
		super(node);
		this.string = string;
	}

	private SlotInteger slot;

	@Override
	protected boolean validateImpl(Environment environment)
	{
		try {
			slot = new SlotInteger(Integer.parseInt(string, 10));
		} catch (NumberFormatException e) {
			environment.reportError("Illegal integer value: " + string, this);
			return false;
		}
		return true;
	}

	@Override
	public Class<?> getType()
	{
		return SlotInteger.class;
	}

	@Override
	public Object getValue()
	{
		return slot;
	}

}
