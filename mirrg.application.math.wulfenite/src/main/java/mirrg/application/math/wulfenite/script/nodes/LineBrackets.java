package mirrg.application.math.wulfenite.script.nodes;

import java.util.ArrayList;

import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.node.IWSLine;
import mirrg.application.math.wulfenite.script.node.WSLineBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LineBrackets extends WSLineBase
{

	public ArrayList<IWSLine> lines;

	public LineBrackets(Node<?> node, ArrayList<IWSLine> lines)
	{
		super(node);
		this.lines = lines;
	}

	@Override
	protected boolean validateImpl(Environment environment)
	{
		boolean[] flag = new boolean[1];
		flag[0] = true;

		environment.doInFrame(() -> {
			for (IWSLine line : lines) {
				if (!line.validate(environment)) {
					flag[0] = false;
					return;
				}
			}
		});
		if (!flag[0]) return false;

		return true;
	}

	@Override
	public void invoke()
	{
		lines.forEach(IWSLine::invoke);
	}

}
