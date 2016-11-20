package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.IWulfeniteScript;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;

public abstract class OperationCast extends ScriptNodeBase
{

	protected IWulfeniteScript from;
	protected Type<?> to;

	public OperationCast(IWulfeniteScript from, Type<?> to)
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
