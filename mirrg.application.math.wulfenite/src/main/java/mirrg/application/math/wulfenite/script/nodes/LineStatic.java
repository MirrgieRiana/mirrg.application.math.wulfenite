package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.node.IWSLine;
import mirrg.application.math.wulfenite.script.node.WSLineBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LineStatic extends WSLineBase
{

	public IWSLine line;

	public LineStatic(Node<?> node, IWSLine line)
	{
		super(node);
		this.line = line;
	}

	@Override
	protected boolean validateImpl(Environment environment)
	{
		boolean flag = true;

		if (!line.validate(environment)) flag = false;

		return flag;
	}

	private boolean finished = false;

	@Override
	public void invoke()
	{
		if (finished) return;

		finished = true;
		line.invoke();
	}

}
