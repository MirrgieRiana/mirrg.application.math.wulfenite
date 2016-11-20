package mirrg.application.math.wulfenite.script;

import javax.swing.text.StyleConstants;

import mirrg.helium.compile.oxygen.editor.TextPaneOxygen;
import mirrg.helium.compile.oxygen.parser.core.Syntax;

public class TextPaneOxygenWulfeniteScript extends TextPaneOxygen<IWulfeniteFormula>
{

	public TextPaneOxygenWulfeniteScript(Syntax<IWulfeniteFormula> syntax)
	{
		super(syntax);
	}

	public void setUnderline(int offset, int length)
	{
		setAttribute(offset, length, s -> {
			s.addAttribute(StyleConstants.Underline, true);
		});
	}

}
