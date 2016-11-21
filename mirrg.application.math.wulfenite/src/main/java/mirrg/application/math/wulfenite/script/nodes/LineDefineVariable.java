package mirrg.application.math.wulfenite.script.nodes;

import java.util.Optional;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.core.Variable;
import mirrg.application.math.wulfenite.script.node.WSLineBase;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class LineDefineVariable extends WSLineBase
{

	public String type;
	public String name;

	public LineDefineVariable(Node<?> node, String type, String name)
	{
		super(node);
		this.type = type;
		this.name = name;
	}

	private Type<?> type2;
	private Variable<?> variable;

	@SuppressWarnings("unchecked")
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

		return true;
	}

	@Override
	public void invoke()
	{

	}

}
