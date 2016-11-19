package mirrg.application.math.wulfenite.script;

import mirrg.helium.compile.oxygen.parser.core.Node;

public abstract class ScriptNodeBase implements IWulfeniteScript
{

	public final int begin;
	public final int end;

	public ScriptNodeBase(Node<?> node)
	{
		begin = node.begin;
		end = node.end;
	}

	public ScriptNodeBase(int begin, int end)
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

}
