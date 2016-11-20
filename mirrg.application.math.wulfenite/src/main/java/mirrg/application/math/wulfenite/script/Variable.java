package mirrg.application.math.wulfenite.script;

import mirrg.application.math.wulfenite.core.types.Type;

public class Variable<T>
{

	public final Type<T> type;
	public T value;

	public Variable(Type<T> type)
	{
		this.type = type;
	}

}
