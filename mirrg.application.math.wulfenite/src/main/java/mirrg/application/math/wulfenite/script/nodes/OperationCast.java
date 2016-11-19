package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.IWulfeniteScript;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;

public abstract class OperationCast extends ScriptNodeBase
{

	protected IWulfeniteScript from;
	protected Class<?> to;

	public OperationCast(IWulfeniteScript from, Class<?> to)
	{
		super(from.getBegin(), from.getEnd());
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean validate(Environment environment)
	{
		if (!from.validate(environment)) return false;
		return true;
	}

	@Override
	public Class<?> getType()
	{
		return to;
	}

}
