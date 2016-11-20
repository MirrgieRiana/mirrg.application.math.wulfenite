package mirrg.application.math.wulfenite.script.node;

import mirrg.helium.compile.oxygen.parser.core.Node;

public abstract class WSLineBase extends WSNodeBase implements IWSLine
{

	public WSLineBase(int begin, int end)
	{
		super(begin, end);
	}

	public WSLineBase(Node<?> node)
	{
		super(node);
	}

}
