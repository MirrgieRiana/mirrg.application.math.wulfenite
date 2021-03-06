package mirrg.application.math.wulfenite.script.nodes;

import java.util.Optional;

import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.core.TypeHelper;
import mirrg.application.math.wulfenite.script.core.Variable;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.application.math.wulfenite.script.node.WSLineBase;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.standard.hydrogen.struct.Tuple;

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
	public IWSFormula formula2;

	@Override
	protected boolean validateImpl(Environment environment)
	{

		// 変数を取得
		Variable<?> variable2;
		{
			Optional<Variable<?>> oVariable = environment.getVariable(name);
			if (!oVariable.isPresent()) {
				environment.reportError("Unknown variable: " + name, this);
				return false;
			}
			variable2 = oVariable.get();
		}
		variable = variable2;

		if (!formula.validate(environment)) return false;

		// キャスト用の式
		IWSFormula formula2;
		{
			Tuple<Integer, IWSFormula> res = TypeHelper.cast(formula, variable.type);
			if (res == null) {
				environment.reportError("Illegal assignment: " + variable.type.name + " = " + formula.getType().name, this);
				return false;
			}
			formula2 = res.getY();
		}
		formula = formula2;

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void invoke()
	{
		((Variable<Object>) variable).value = formula.getValue();
	}

}
