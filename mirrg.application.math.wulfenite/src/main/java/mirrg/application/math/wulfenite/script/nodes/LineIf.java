package mirrg.application.math.wulfenite.script.nodes;

import mirrg.application.math.wulfenite.core.types.SlotBoolean;
import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.core.TypeHelper;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.application.math.wulfenite.script.node.IWSLine;
import mirrg.application.math.wulfenite.script.node.WSLineBase;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class LineIf extends WSLineBase
{

	public IWSFormula condition;
	public IWSLine lineThen;
	public IWSLine lineElse;

	public LineIf(Node<?> node, IWSFormula condition, IWSLine lineThen, IWSLine lineElse)
	{
		super(node);
		this.condition = condition;
		this.lineThen = lineThen;
		this.lineElse = lineElse;
	}

	public LineIf(Node<?> node, IWSFormula condition, IWSLine lineThen)
	{
		super(node);
		this.condition = condition;
		this.lineThen = lineThen;
		this.lineElse = null;
	}

	@Override
	protected boolean validateImpl(Environment environment)
	{
		boolean flag = true;

		if (condition.validate(environment)) {

			// キャスト
			{
				Tuple<Integer, IWSFormula> res = TypeHelper.cast(condition, Type.BOOLEAN);
				if (res == null) {
					environment.reportError("Type error: " + condition.getType().name + " != " + Type.BOOLEAN.name, condition);
					flag = false;
				} else {
					condition = res.getY();
				}
			}

		} else {
			flag = false;
		}

		if (!lineThen.validate(environment)) flag = false;

		if (lineElse != null) if (!lineElse.validate(environment)) flag = false;

		return flag;
	}

	@Override
	public void invoke()
	{
		if (((SlotBoolean) condition.getValue()).value) {
			lineThen.invoke();
		} else {
			if (lineElse != null) lineElse.invoke();
		}
	}

}
