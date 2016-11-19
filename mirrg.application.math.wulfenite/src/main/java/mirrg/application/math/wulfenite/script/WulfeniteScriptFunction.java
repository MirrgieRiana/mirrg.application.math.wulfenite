package mirrg.application.math.wulfenite.script;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class WulfeniteScriptFunction implements IWulfeniteScriptFunction
{

	public String name;
	public Class<?> type;
	public ArrayList<Class<?>> args;

	public WulfeniteScriptFunction(String name, Class<?> type, Class<?>... args)
	{
		this.name = name;
		this.type = type;
		this.args = new ArrayList<>(Arrays.asList(args));
	}

	@Override
	public Class<?> getType()
	{
		return type;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public ArrayList<Class<?>> getArgumentsType()
	{
		return args;
	}

}
