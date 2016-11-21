package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.types.SlotString;
import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.node.WSFormulaBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LiteralString extends WSFormulaBase
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
	public Type<?> getType()
	{
		return Type.STRING;
	}

	@Override
	public Object getValue()
	{
		return slot;
	}

}
