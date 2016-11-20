package mirrg.application.math.wulfenite.script;

import mirrg.application.math.wulfenite.core.types.Type;

public interface IWulfeniteScript
{

	public boolean validate(Environment environment);

	public Type<?> getType();

	public Object getValue();

	public int getBegin();

	public int getEnd();

}
