package mirrg.application.math.wulfenite.script;

import java.awt.Color;

import mirrg.helium.compile.oxygen.editor.IProviderColor;
import mirrg.helium.compile.oxygen.parser.core.Node;

public abstract class ScriptNodeBase implements IWulfeniteFormula, IProviderColor
{

	public final int begin;
	public final int end;

	public ScriptNodeBase(Node<?> node)
	{
		begin = node.begin;
		end = node.end;
	}

	public ScriptNodeBase(int begin, int end)
	{
		this.begin = begin;
		this.end = end;
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
	public Color getColor()
	{
		if (isValid) {
			return TypeHelper.getTokenColor(getType());
		} else {
			return Color.red;
		}
	}

}
