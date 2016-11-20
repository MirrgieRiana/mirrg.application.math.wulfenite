package mirrg.application.math.wulfenite.script.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.application.math.wulfenite.script.node.IWSLine;
import mirrg.application.math.wulfenite.script.node.IWSNode;
import mirrg.application.math.wulfenite.script.node.WSFormulaBase;
import mirrg.helium.compile.oxygen.editor.IProviderChildren;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class ExpressionRoot extends WSFormulaBase implements IProviderChildren
{

	public ArrayList<IWSLine> lines;
	public IWSFormula formula;

	public ExpressionRoot(Node<?> node, ArrayList<IWSLine> lines, IWSFormula formula)
	{
		super(node);
		this.lines = lines;
		this.formula = formula;
	}

	@Override
	protected boolean validateImpl(Environment environment)
	{

		for (IWSLine line : lines) {
			if (!line.validate(environment)) return false;
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
			lines.stream(),
			Stream.of(formula))
			.map(IWSNode::createNode)
			.collect(Collectors.toCollection(ArrayList::new));
	}

}
