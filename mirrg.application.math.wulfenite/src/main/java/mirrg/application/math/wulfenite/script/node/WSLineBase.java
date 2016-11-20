package mirrg.application.math.wulfenite.script.node;

import mirrg.application.math.wulfenite.script.Environment;
import mirrg.helium.compile.oxygen.parser.core.Node;

public abstract class WSLineBase implements IWSLine
{

	public final int begin;
	public final int end;

	public WSLineBase(Node<?> node)
	{
		begin = node.begin;
		end = node.end;
	}

	public WSLineBase(int begin, int end)
	{
		this.begin = begin;
		this.end = end;
	}

	@Override
	public int getBegin()
	{
		return begin;
	}

	@Override
	public int getEnd()
	{
		return end;
	}

	public boolean isValid;

	@Override
	public boolean validate(Environment environment)
	{
		isValid = validateImpl(environment);
		return isValid;
	}

	protected abstract boolean validateImpl(Environment environment);

}
