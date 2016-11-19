package mirrg.application.math.wulfenite.script.nodes;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.IWulfeniteScript;
import mirrg.application.math.wulfenite.script.IWulfeniteScriptFunction;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class OperationFunction extends ScriptNodeBase
{

	private String name;
	private IWulfeniteScript[] args;

	public OperationFunction(int begin, int end, String name, IWulfeniteScript... args)
	{
		super(begin, end);
		this.name = name;
		this.args = args;
		args2 = new Object[args.length];
	}

	private IWulfeniteScriptFunction function;
	private IWulfeniteScript[] args3;
	private Function<Object[], Object> function2;

	@Override
	public boolean validate(Environment environment)
	{
		boolean flag = true;

		// validate args
		{
			for (IWulfeniteScript wulfeniteScript : args) {
				if (!wulfeniteScript.validate(environment)) flag = false;
			}
		}
		if (!flag) return false;

		// validate function
		{
			ArrayList<Tuple<IWulfeniteScriptFunction, ArrayList<IWulfeniteScript>>> functions = environment.getFunction(name, args);

			if (functions.size() == 1) {
				function = functions.get(0).getX();
				args3 = functions.get(0).getY().toArray(new IWulfeniteScript[0]);
				function2 = function.createValueProvider();
			} else if (functions.size() == 0) {
				environment.reportError("No such function: " + name + "(" + Stream.of(args)
					.map(IWulfeniteScript::getType)
					.map(Class::getSimpleName)
					.collect(Collectors.joining(", ")) + ")", this);
				flag = false;
			} else {
				environment.reportError("Ambiguous function call: " + name + "(" + Stream.of(args)
					.map(IWulfeniteScript::getType)
					.map(Class::getSimpleName)
					.collect(Collectors.joining(", ")) + ")", this);
				flag = true;
			}
		}
		if (!flag) return false;

		return true;
	}

	@Override
	public Class<?> getType()
	{
		return function.getType();
	}

	private Object[] args2;

	@Override
	public Object getValue()
	{
		for (int i = 0; i < args.length; i++) {
			args2[i] = args3[i].getValue();
		}
		return function2.apply(args2);
	}

}
