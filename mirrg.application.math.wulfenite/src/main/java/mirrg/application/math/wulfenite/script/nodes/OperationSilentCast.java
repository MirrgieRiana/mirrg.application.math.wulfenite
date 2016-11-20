package mirrg.application.math.wulfenite.script.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.application.math.wulfenite.script.node.WSFormulaBase;
import mirrg.helium.compile.oxygen.editor.IProviderChildren;
import mirrg.helium.compile.oxygen.parser.core.Node;

public abstract class OperationSilentCast extends WSFormulaBase implements IProviderChildren
{

	protected IWSFormula from;
	protected Type<?> to;

	public OperationSilentCast(IWSFormula from, Type<?> to)
	{
		super(from.getBegin(), from.getEnd());
		this.from = from;
		this.to = to;
	}

	@Override
	protected boolean validateImpl(Environment environment)
	{
		if (!from.validate(environment)) return false;
		return true;
	}

	@Override
	public Type<?> getType()
	{
		return to;
	}

	@Override
	public List<Node<?>> getChildren()
	{
		return Stream.of(from.getNode())
			.collect(Collectors.toCollection(ArrayList::new));
	}

}
