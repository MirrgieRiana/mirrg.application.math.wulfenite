package mirrg.application.math.wulfenite.script;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.util.function.Consumer;

import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSlot;
import mirrg.helium.math.hydrogen.complex.StructureComplex;

public class WulfeniteScript
{

	public static Syntax<Consumer<StructureComplex>> getSyntax()
	{
		SyntaxSlot<Consumer<StructureComplex>> expression = slot();

		expression.syntax = pack(pack(regex("[0-9]+"),
			t -> Integer.parseInt(t, 10)),
			t -> c -> {
				c.re = c.re * t;
			});

		return expression;
	}

}
