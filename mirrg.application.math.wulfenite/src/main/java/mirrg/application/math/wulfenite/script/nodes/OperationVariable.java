package mirrg.application.math.wulfenite.script.nodes;

import java.util.Optional;

import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;
import mirrg.application.math.wulfenite.script.Variable;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class OperationVariable extends ScriptNodeBase
{

	private String name;

	public OperationVariable(Node<?> node, String name)
	{
		super(node);
		this.name = name;
	}

	private Variable variable;

	@Override
	public boolean validate(Environment environment)
	{
		boolean flag = true;

		{
			Optional<Variable> oVariable = environment.getVariable(name);

			if (oVariable.isPresent()) {
				variable = oVariable.get();
			} else {
				environment.reportError("No such variable: " + name, this);
				flag = false;
			}
		}
		if (!flag) return false;

		return true;
	}

	@Override
	public Class<?> getType()
	{
		return variable.type;
	}

	@Override
	public Object getValue()
	{
		return variable.value;
	}

}
