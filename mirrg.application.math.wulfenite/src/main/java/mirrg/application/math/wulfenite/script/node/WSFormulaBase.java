package mirrg.application.math.wulfenite.script.node;

import java.awt.Color;

import mirrg.application.math.wulfenite.script.TypeHelper;
import mirrg.helium.compile.oxygen.editor.IProviderColor;
import mirrg.helium.compile.oxygen.parser.core.Node;

public abstract class WSFormulaBase extends WSNodeBase implements IWSFormula, IProviderColor
{

	public WSFormulaBase(int begin, int end)
	{
		super(begin, end);
	}

	public WSFormulaBase(Node<?> node)
	{
		super(node);
	}

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
