package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.node.WSLineBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LineVoid extends WSLineBase
{

	public LineVoid(Node<?> node)
	{
		super(node);
	}

	@Override
	protected boolean validateImpl(Environment environment)
	{
		return true;
	}

	@Override
	public void invoke()
	{

	}

}
