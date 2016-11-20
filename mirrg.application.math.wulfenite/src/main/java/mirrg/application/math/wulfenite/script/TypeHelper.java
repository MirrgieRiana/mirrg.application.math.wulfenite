package mirrg.application.math.wulfenite.script;

import static mirrg.application.math.wulfenite.core.types.Type.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.function.BiConsumer;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.nodes.OperationCast;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class TypeHelper
{

	private static Hashtable<Type<?>, Color> colors = new Hashtable<>();
	static {
		colors.put(Type.INTEGER, Color.decode("#f08000"));
		colors.put(Type.DOUBLE, Color.decode("#000080"));
		colors.put(Type.COMPLEX, Color.decode("#600000"));
		colors.put(Type.COLOR, Color.decode("#ff8000"));
		colors.put(Type.BOOLEAN, Color.decode("#808000"));
		colors.put(Type.STRING, Color.decode("#ff00ff"));
	}

	private static ArrayList<CastEntry<?, ?>> casters = new ArrayList<>();
	static {
		r(S, C, 1, (z, a) -> z.value = a.re + "+" + a.im + "i");
		r(S, D, 2, (z, a) -> z.value = a.toString());
		r(C, D, 1, (z, a) -> z.re = a.value);
		r(S, I, 3, (z, a) -> z.value = a.toString());
		r(C, I, 2, (z, a) -> z.re = a.value);
		r(D, I, 1, (z, a) -> z.value = a.value);
		r(S, B, 1, (z, a) -> z.value = a.toString());
		r(S, Co, 1, (z, a) -> z.value = a.toString());
	}

	public static Color getTokenColor(Type<?> type)
	{
		Color color = colors.get(type);
		if (color != null) return color;
		return Color.black;
	}

	public static Tuple<Integer, IWulfeniteFormula> cast(IWulfeniteFormula from, Type<?> to)
	{
		if (from.getType() == to) return new Tuple<>(0, from);
		return casters.stream()
			.filter(t -> t.match(from, to))
			.findFirst()
			.map(t -> new Tuple<>(t.distance, t.apply(from)))
			.orElse(null);
	}

	private static <O, I> void r(Type<O> to, Type<I> from, int distance, BiConsumer<O, I> consumer)
	{
		casters.add(new CastEntry<>(to, from, distance, consumer));
	}

	private static class CastEntry<O, I>
	{

		private Type<O> to;
		private Type<I> from;
		private int distance;
		private BiConsumer<O, I> consumer;

		private CastEntry(Type<O> to, Type<I> from, int distance, BiConsumer<O, I> consumer)
		{
			this.to = to;
			this.from = from;
			this.distance = distance;
			this.consumer = consumer;
		}

		public boolean match(IWulfeniteFormula from, Type<?> to)
		{
			return from.getType() == this.from && to == this.to;
		}

		public IWulfeniteFormula apply(IWulfeniteFormula from)
		{
			O slot = to.create();
			return new OperationCast(from, to) {

				@SuppressWarnings("unchecked")
				@Override
				public Object getValue()
				{
					consumer.accept(slot, (I) from.getValue());
					return slot;
				}

			};
		}

	}

}
