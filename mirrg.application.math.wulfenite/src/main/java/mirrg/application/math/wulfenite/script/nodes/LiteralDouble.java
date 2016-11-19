package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.SlotDouble;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LiteralDouble extends ScriptNodeBase
{

	private String string;

	public LiteralDouble(Node<?> node, String string)
	{
		super(node);
		this.string = string;
	}

	private SlotDouble slot;

	@Override
	public boolean validate(Environment environment)
	{
		slot = new SlotDouble(Double.parseDouble(string));
		return true;
	}

	@Override
	public Class<?> getType()
	{
		return SlotDouble.class;
	}

	@Override
	public Object getValue()
	{
		return slot;
	}

}
