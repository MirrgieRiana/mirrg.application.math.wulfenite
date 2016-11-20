package mirrg.application.math.wulfenite.script;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class WulfeniteScriptFunction implements IWulfeniteScriptFunction
{

	public Class<?> type;
	public ArrayList<Class<?>> args;

	public WulfeniteScriptFunction(Class<?> type, Class<?>... args)
	{
		this.type = type;
		this.args = new ArrayList<>(Arrays.asList(args));
	}

	@Override
	public Class<?> getType()
	{
		return type;
	}

	@Override
	public ArrayList<Class<?>> getArgumentsType()
	{
		return args;
	}

}
