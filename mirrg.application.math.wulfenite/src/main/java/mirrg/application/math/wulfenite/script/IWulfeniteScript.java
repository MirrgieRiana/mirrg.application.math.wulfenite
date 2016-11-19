package mirrg.application.math.wulfenite.script;

public interface IWulfeniteScript
{

	public boolean validate(Environment environment);

	public Class<?> getType();

	public Object getValue();

	public int getBegin();

	public int getEnd();

}
