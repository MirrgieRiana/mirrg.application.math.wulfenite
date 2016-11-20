package mirrg.application.math.wulfenite.script.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.IWulfeniteFormula;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;
import mirrg.helium.compile.oxygen.editor.IProviderChildren;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.standard.hydrogen.struct.Struct2;

public class ExpressionRoot extends ScriptNodeBase implements IProviderChildren
{

	public ArrayList<Struct2<String, IWulfeniteFormula>> lines;
	public IWulfeniteFormula formula;

	public ExpressionRoot(Node<?> node, ArrayList<Struct2<String, IWulfeniteFormula>> lines, IWulfeniteFormula formula)
	{
		super(node);
		this.lines = lines;
		this.formula = formula;
	}

	@Override
	protected boolean validateImpl(Environment environment)
	{

		for (Struct2<String, IWulfeniteFormula> line : lines) {
			if (!line.y.validate(environment)) return false;
			if (environment.getVariable(line.x).isPresent()) {
				environment.reportError("Duplicate variable: " + line.x, this);
				return false;
			}
			environment.addVariable(line.x, line.y.getType());
		}

		if (!formula.validate(environment)) return false;

		return true;
	}

	@Override
	public Type<?> getType()
	{
		return formula.getType();
	}

	@Override
	public Object getValue()
	{
		return formula.getValue();
	}

	@Override
	public List<Node<?>> getChildren()
	{
		return Stream.concat(
			lines.stream()
				.map(Struct2::getY),
			Stream.of(formula))
			.map(a -> new Node<Object>(null, null, a.getBegin(), a.getEnd(), a))
			.collect(Collectors.toCollection(ArrayList::new));
	}

}
