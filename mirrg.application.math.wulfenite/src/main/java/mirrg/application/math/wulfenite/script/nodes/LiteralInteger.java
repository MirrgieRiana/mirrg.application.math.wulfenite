package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.types.SlotInteger;
import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.node.WSFormulaBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LiteralInteger extends WSFormulaBase
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
	public Type<?> getType()
	{
		return Type.INTEGER;
	}

	@Override
	public Object getValue()
	{
		return slot;
	}

}
