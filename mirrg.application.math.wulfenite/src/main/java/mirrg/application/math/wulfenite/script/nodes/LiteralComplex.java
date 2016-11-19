package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.math.hydrogen.complex.StructureComplex;

public class LiteralComplex extends ScriptNodeBase
{

	private String string;

	public LiteralComplex(Node<?> node, String string)
	{
		super(node);
		this.string = string;
	}

	private StructureComplex slot;

	@Override
	public boolean validate(Environment environment)
	{
		slot = new StructureComplex(0, Double.parseDouble(string));
		return true;
	}

	@Override
	public Class<?> getType()
	{
		return StructureComplex.class;
	}

	@Override
	public Object getValue()
	{
		return slot;
	}

}
