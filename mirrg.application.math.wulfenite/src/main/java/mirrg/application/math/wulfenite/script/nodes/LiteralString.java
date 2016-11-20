package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.types.SlotString;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LiteralString extends ScriptNodeBase
{

	private String string;

	public LiteralString(Node<?> node, String string)
	{
		super(node);
		this.string = string;
	}

	private SlotString slot;

	@Override
	protected boolean validateImpl(Environment environment)
	{
		slot = new SlotString(string);
		return true;
	}

	@Override
	public Class<?> getType()
	{
		return SlotString.class;
	}

	@Override
	public Object getValue()
	{
		return slot;
	}

}
