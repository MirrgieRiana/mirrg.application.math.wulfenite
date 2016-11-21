package mirrg.application.math.wulfenite.script.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class Frame
{

	private Hashtable<String, Variable<?>> variables = new Hashtable<>();
	public final Frame parent;

	public Frame(Frame parent)
	{
		this.parent = parent;
	}

	public Frame()
	{
		this.parent = null;
	}

	public <T> Variable<T> addVariable(String name, Type<T> type)
	{
		Variable<T> variable = new Variable<>(type);
		variables.put(name, variable);
		return variable;
	}

	public Optional<Variable<?>> getVariable(String name)
	{
		Variable<?> variable = variables.get(name);
		if (variable != null) return Optional.of(variable);
		if (parent != null) return parent.getVariable(name);
		return Optional.empty();
	}

	public void getVariables(ArrayList<Tuple<String, Variable<?>>> dest)
	{
		variables.entrySet().stream()
			.forEach(s -> dest.add(new Tuple<>(s.getKey(), s.getValue())));
		if (parent != null) parent.getVariables(dest);
	}

}
