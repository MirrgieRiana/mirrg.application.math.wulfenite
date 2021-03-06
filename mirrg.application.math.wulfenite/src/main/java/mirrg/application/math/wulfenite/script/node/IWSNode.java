package mirrg.application.math.wulfenite.script.node;

import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.helium.compile.oxygen.parser.core.Node;

public interface IWSNode
{

	public boolean validate(Environment environment);

	public int getBegin();

	public int getEnd();

	public Node<?> getNode();

}
