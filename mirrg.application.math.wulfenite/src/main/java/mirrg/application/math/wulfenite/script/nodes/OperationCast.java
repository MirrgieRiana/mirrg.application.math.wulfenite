package mirrg.application.math.wulfenite.script.nodes;

import java.util.Optional;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.core.TypeHelper;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.application.math.wulfenite.script.node.WSFormulaBase;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class OperationCast extends WSFormulaBase
{

	protected String type;
	protected IWSFormula formula;

	public OperationCast(int begin, int end, String type, IWSFormula formula)
	{
		super(begin, end);
		this.type = type;
		this.formula = formula;
	}

	private Type<?> type2;

	@Override
	protected boolean validateImpl(Environment environment)
	{

		// 型を取得
		{
			Optional<Type<?>> oType = Type.getType(type);
			if (!oType.isPresent()) {
				environment.reportError("Unknown type name: " + type, this);
				return false;
			}
			type2 = oType.get();
		}

		if (!formula.validate(environment)) return false;

		// キャスト用の式
		IWSFormula formula2;
		{
			Tuple<Integer, IWSFormula> res = TypeHelper.cast(formula, type2);
			if (res == null) {
				environment.reportError("Illegal cast: (" + type2.name + ") " + formula.getType().name, this);
				return false;
			}
			formula2 = res.getY();
		}
		formula = formula2;

		return true;
	}

	@Override
	public Type<?> getType()
	{
		return type2;
	}

	@Override
	public Object getValue()
	{
		return formula.getValue();
	}

}
