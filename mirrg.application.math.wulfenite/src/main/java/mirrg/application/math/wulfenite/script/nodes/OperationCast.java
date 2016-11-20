package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.application.math.wulfenite.script.node.WSFormulaBase;

public abstract class OperationCast extends WSFormulaBase
{

	protected IWSFormula from;
	protected Type<?> to;

	public OperationCast(IWSFormula from, Type<?> to)
	{
		super(from.getBegin(), from.getEnd());
		this.from = from;
		this.to = to;
	}

	@Override
	protected boolean validateImpl(Environment environment)
	{
		if (!from.validate(environment)) return false;
		return true;
	}

	@Override
	public Type<?> getType()
	{
		return to;
	}

}
