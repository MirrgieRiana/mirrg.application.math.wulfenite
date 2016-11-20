package mirrg.application.math.wulfenite.script.function;

import java.util.ArrayList;
import java.util.Arrays;

import mirrg.application.math.wulfenite.core.types.Type;

public abstract class WSFunction<T> implements IWSFunction
{

	public Type<T> type;
	public ArrayList<Type<?>> args;

	public WSFunction(Type<T> type, Type<?>... args)
	{
		this.type = type;
		this.args = new ArrayList<>(Arrays.asList(args));
	}

	@Override
	public Type<T> getType()
	{
		return type;
	}

	@Override
	public ArrayList<Type<?>> getArgumentsType()
	{
		return args;
	}

}
