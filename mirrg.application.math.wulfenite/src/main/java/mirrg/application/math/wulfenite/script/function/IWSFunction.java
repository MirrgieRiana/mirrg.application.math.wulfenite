package mirrg.application.math.wulfenite.script.function;

import java.util.ArrayList;
import java.util.function.Function;

import mirrg.application.math.wulfenite.core.types.Type;

public interface IWSFunction
{

	public Type<?> getType();

	public ArrayList<Type<?>> getArgumentsType();

	public Function<Object[], Object> createValueProvider();

}
