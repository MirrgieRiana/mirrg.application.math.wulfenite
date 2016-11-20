package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.script.Environment;
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

	@Override
	protected boolean validateImpl(Environment environment)
	{
		if (!formula.validate(environment)) return false;
		if (environment.getVariable(name).isPresent()) {
			environment.reportError("Duplicate variable: " + name, this);
			return false;
		}
		environment.addVariable(name, formula.getType());

		return true;
	}

}
