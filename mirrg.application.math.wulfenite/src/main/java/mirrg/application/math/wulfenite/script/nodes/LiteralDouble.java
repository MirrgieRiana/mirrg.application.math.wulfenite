package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.types.SlotDouble;
import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.node.WSFormulaBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LiteralDouble extends WSFormulaBase
{

	private String string;

	public LiteralDouble(Node<?> node, String string)
	{
		super(node);
		this.string = string;
	}

	private SlotDouble slot;

	@Override
	protected boolean validateImpl(Environment environment)
	{
		slot = new SlotDouble(Double.parseDouble(string));
		return true;
	}

	@Override
	public Type<?> getType()
	{
		return Type.DOUBLE;
	}

	@Override
	public Object getValue()
	{
		return slot;
	}

}
