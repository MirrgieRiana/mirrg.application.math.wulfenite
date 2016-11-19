package mirrg.application.math.wulfenite.script;

import java.util.ArrayList;
import java.util.function.Function;

public interface IWulfeniteScriptFunction
{

	public Class<?> getType();

	public String getName();

	public ArrayList<Class<?>> getArgumentsType();

	public Function<Object[], Object> createValueProvider();

}
