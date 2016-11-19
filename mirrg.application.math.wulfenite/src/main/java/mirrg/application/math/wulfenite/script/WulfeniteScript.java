package mirrg.application.math.wulfenite.script;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import mirrg.application.math.wulfenite.core.SlotColor;
import mirrg.application.math.wulfenite.core.SlotDouble;
import mirrg.application.math.wulfenite.core.SlotInteger;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSlot;

public class WulfeniteScript
{

	public static Syntax<IWulfeniteScript> getSyntax()
	{
		SyntaxSlot<IWulfeniteScript> expression = slot();

		Syntax<String> comment = named(regex("[ \t\r\n]*"), "Comment");

		Syntax<IWulfeniteScript> literalDouble = pack(named(regex("[0-9]+\\.[0-9]+"), "Double"),
			t -> new LiteralDouble(Double.parseDouble(t)));
		Syntax<IWulfeniteScript> literalInteger = pack(named(regex("[0-9]+"), "Integer"),
			t -> new LiteralInteger(Integer.parseInt(t, 10)));
		Syntax<IWulfeniteScript> literalColor = named(extract((IWulfeniteScript) null)
			.and(string("#"))
			.extract(or((IWulfeniteScript) null)
				.or(pack(regex("[0-9a-zA-Z]{6}"),
					t -> new LiteralColor(Integer.parseInt(t, 16))))
				.or(pack(regex("[0-9a-zA-Z]{3}"),
					t -> new LiteralColor(Integer.parseInt("" +
						t.charAt(0) + t.charAt(0) +
						t.charAt(1) + t.charAt(1) +
						t.charAt(2) + t.charAt(2), 16))))), "Color");
		Syntax<IWulfeniteScript> bracket = extract((IWulfeniteScript) null)
			.and(string("("))
			.and(comment)
			.extract(expression)
			.and(comment)
			.and(string(")"));
		Syntax<IWulfeniteScript> factor = or((IWulfeniteScript) null)
			.or(literalDouble)
			.or(literalInteger)
			.or(literalColor)
			.or(bracket);

		expression.syntax = factor;

		return extract((IWulfeniteScript) null)
			.and(comment)
			.extract(expression)
			.and(comment)
			.and(named(string(""), "EOF"));
	}

	public static class LiteralInteger implements IWulfeniteScript
	{

		private SlotInteger slot;

		public LiteralInteger(int value)
		{
			slot = new SlotInteger(value);
		}

		@Override
		public Object getValue()
		{
			return slot;
		}

		@Override
		public boolean validate()
		{
			return true;
		}

		@Override
		public Class<?> getType()
		{
			return SlotInteger.class;
		}

	}

	public static class LiteralColor implements IWulfeniteScript
	{

		private SlotColor slot;

		public LiteralColor(int value)
		{
			slot = new SlotColor(value);
		}

		@Override
		public Object getValue()
		{
			return slot;
		}

		@Override
		public boolean validate()
		{
			return true;
		}

		@Override
		public Class<?> getType()
		{
			return SlotColor.class;
		}

	}

	public static class LiteralDouble implements IWulfeniteScript
	{

		private SlotDouble slot;

		public LiteralDouble(double value)
		{
			slot = new SlotDouble(value);
		}

		@Override
		public Object getValue()
		{
			return slot;
		}

		@Override
		public boolean validate()
		{
			return true;
		}

		@Override
		public Class<?> getType()
		{
			return SlotDouble.class;
		}

	}

}
