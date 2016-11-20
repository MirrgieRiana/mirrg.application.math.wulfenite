package mirrg.application.math.wulfenite.script;

import mirrg.application.math.wulfenite.core.types.Type;

public interface IWulfeniteFormula
{

	public boolean validate(Environment environment);

	public Type<?> getType();

	public Object getValue();

	public int getBegin();

	public int getEnd();

}
