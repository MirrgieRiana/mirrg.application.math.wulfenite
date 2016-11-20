package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.Variable;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.application.math.wulfenite.script.node.WSLineBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LineAssignment extends WSLineBase
{

	public String name;
	public IWSFormula formula;

	public LineAssignment(Node<?> node, String name, IWSFormula formula)
	{
		super(node);
		this.name = name;
		this.formula = formula;
	}

	private Variable<?> variable;

	@Override
	protected boolean validateImpl(Environment environment)
	{
		if (!formula.validate(environment)) return false;
		if (environment.getVariable(name).isPresent()) {
			environment.reportError("Duplicate variable: " + name, this);
			return false;
		}
		variable = environment.addVariable(name, formula.getType());

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void invoke()
	{
		((Variable<Object>) variable).value = formula.getValue();
	}

}
