package mirrg.application.math.wulfenite.script.node;

import java.util.ArrayList;

import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.helium.compile.oxygen.parser.core.IListenerNode;
import mirrg.helium.compile.oxygen.parser.core.Node;

public abstract class WSNodeBase implements IWSNode, IListenerNode
{

	public Node<?> node;
	public final int begin;
	public final int end;

	public WSNodeBase(int begin, int end)
	{
		this.node = new Node<>(null, new ArrayList<>(), begin, end, this);
		this.begin = begin;
		this.end = end;
	}

	public WSNodeBase(Node<?> node)
	{
		this.node = node;
		begin = node.begin;
		end = node.end;
	}

	@Override
	public int getBegin()
	{
		return begin;
	}

	@Override
	public int getEnd()
	{
		return end;
	}

	public boolean isValid;

	@Override
	public boolean validate(Environment environment)
	{
		isValid = validateImpl(environment);
		return isValid;
	}

	protected abstract boolean validateImpl(Environment environment);

	@Override
	public Node<?> getNode()
	{
		return node;
	}

	@Override
	public void setNode(Node<?> node)
	{
		this.node = node;
	}

}
