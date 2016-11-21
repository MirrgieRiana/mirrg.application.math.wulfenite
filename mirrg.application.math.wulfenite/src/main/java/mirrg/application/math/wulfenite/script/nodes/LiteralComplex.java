package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.node.WSFormulaBase;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.math.hydrogen.complex.StructureComplex;

public class LiteralComplex extends WSFormulaBase
{

	private String string;

	public LiteralComplex(Node<?> node, String string)
	{
		super(node);
		this.string = string;
	}

	private StructureComplex slot;

	@Override
	protected boolean validateImpl(Environment environment)
	{
		slot = new StructureComplex(0, Double.parseDouble(string));
		return true;
	}

	@Override
	public Type<?> getType()
	{
		return Type.COMPLEX;
	}

	@Override
	public Object getValue()
	{
		return slot;
	}

}
