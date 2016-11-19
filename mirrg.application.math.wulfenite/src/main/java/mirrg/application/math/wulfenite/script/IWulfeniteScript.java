package mirrg.application.math.wulfenite.script;

public interface IWulfeniteScript
{

	public boolean validate();

	public Class<?> getType();

	public Object getValue();

}
