package mirrg.application.math.wulfenite.script;

public class Variable<T>
{

	public final Class<T> type;
	public T value;

	public Variable(Class<T> type)
	{
		this.type = type;
	}

}
