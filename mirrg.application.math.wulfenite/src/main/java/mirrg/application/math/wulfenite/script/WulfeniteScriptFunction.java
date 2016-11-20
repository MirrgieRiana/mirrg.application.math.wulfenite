package mirrg.application.math.wulfenite.script;

import java.util.ArrayList;
import java.util.Arrays;

import mirrg.application.math.wulfenite.core.types.Type;

public abstract class WulfeniteScriptFunction implements IWulfeniteScriptFunction
{

	public Type<?> type;
	public ArrayList<Type<?>> args;

	public WulfeniteScriptFunction(Type<?> type, Type<?>... args)
	{
		this.type = type;
		this.args = new ArrayList<>(Arrays.asList(args));
	}

	@Override
	public Type<?> getType()
	{
		return type;
	}

	@Override
	public ArrayList<Type<?>> getArgumentsType()
	{
		return args;
	}

}
