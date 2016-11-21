package mirrg.application.math.wulfenite.script.nodes;

import java.util.Optional;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.core.TypeHelper;
import mirrg.application.math.wulfenite.script.core.Variable;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.application.math.wulfenite.script.node.WSLineBase;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class LineDefineVariableAssignment extends WSLineBase
{

	public String type;
	public String name;
	public IWSFormula formula;

	public LineDefineVariableAssignment(Node<?> node, String type, String name, IWSFormula formula)
	{
		super(node);
		this.type = type;
		this.name = name;
		this.formula = formula;
	}

	private Variable<?> variable;

	@SuppressWarnings("unchecked")
	@Override
	protected boolean validateImpl(Environment environment)
	{

		// 右辺をチェック
		if (!formula.validate(environment)) return false;

		// 型を取得
		Type<?> type2;
		if (type.equals("var")) {
			type2 = formula.getType();
		} else {
			Optional<Type<?>> oType = Type.getType(type);
			if (!oType.isPresent()) {
				environment.reportError("Unknown type name: " + type, this);
				return false;
			}
			type2 = oType.get();
		}

		// 変数作る
		Variable<?> variable2;
		{
			if (environment.getVariable(name).isPresent()) {
				environment.reportError("Duplicate variable: " + name, this);
				return false;
			}
			variable2 = environment.addVariable(name, type2);
			((Variable<Object>) variable2).value = type2.create();
		}
		variable = variable2;

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
