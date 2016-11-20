package mirrg.application.math.wulfenite.script.node;

import mirrg.application.math.wulfenite.core.types.Type;

public interface IWSFormula extends IWSNode
{

	public Type<?> getType();

	public Object getValue();

}
