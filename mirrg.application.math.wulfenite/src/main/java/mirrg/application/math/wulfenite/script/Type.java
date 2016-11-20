package mirrg.application.math.wulfenite.script;

import java.awt.Color;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import mirrg.application.math.wulfenite.core.types.SlotBoolean;
import mirrg.application.math.wulfenite.core.types.SlotColor;
import mirrg.application.math.wulfenite.core.types.SlotDouble;
import mirrg.application.math.wulfenite.core.types.SlotInteger;
import mirrg.application.math.wulfenite.core.types.SlotString;
import mirrg.application.math.wulfenite.script.nodes.OperationCast;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.standard.hydrogen.struct.Tuple;

public class Type
{

	public static Color getTokenColor(Class<?> type)
	{
		if (type == SlotInteger.class) {
			return Color.decode("#f08000");
		} else if (type == SlotDouble.class) {
			return Color.decode("#000080");
		} else if (type == StructureComplex.class) {
			return Color.decode("#600000");
		} else if (type == SlotColor.class) {
			return Color.decode("#ff8000");
		} else if (type == SlotBoolean.class) {
			return Color.decode("#808000");
		} else if (type == SlotString.class) {
			return Color.decode("#ff00ff");
		} else {
			return Color.black;
		}
	}

	public static Tuple<Integer, IWulfeniteScript> cast(IWulfeniteScript from, Class<?> to)
	{
		if (from.getType() == to) return new Tuple<>(0, from);
		return casters.stream()
			.filter(t -> t.match(from, to))
			.findFirst()
			.map(t -> new Tuple<>(t.distance, t.apply(from)))
			.orElse(null);
	}

	private static ArrayList<Entry<?, ?>> casters = new ArrayList<>();
	static {
		Class<SlotString> S = SlotString.class;
		Class<StructureComplex> C = StructureComplex.class;
		Class<SlotDouble> D = SlotDouble.class;
		Class<SlotInteger> I = SlotInteger.class;
		Class<SlotBoolean> B = SlotBoolean.class;
		Class<SlotColor> Co = SlotColor.class;

		r(S, C, 1, SlotString::new, (z, a) -> z.value = a.re + "+" + a.im + "i");
		r(S, D, 2, SlotString::new, (z, a) -> z.value = a.toString());
		r(C, D, 1, StructureComplex::new, (z, a) -> z.re = a.value);
		r(S, I, 3, SlotString::new, (z, a) -> z.value = a.toString());
		r(C, I, 2, StructureComplex::new, (z, a) -> z.re = a.value);
		r(D, I, 1, SlotDouble::new, (z, a) -> z.value = a.value);
		r(S, B, 1, SlotString::new, (z, a) -> z.value = a.toString());
		r(S, Co, 1, SlotString::new, (z, a) -> z.value = a.toString());
	}

	private static <O, I> void r(Class<O> to, Class<I> from, int distance, Supplier<O> supplier, BiConsumer<O, I> consumer)
	{
		casters.add(new Entry<>(to, from, distance, supplier, consumer));
	}

	private static class Entry<O, I>
	{

		private Class<?> to;
		private Class<?> from;
		private int distance;
		private Supplier<O> supplier;
		private BiConsumer<O, I> consumer;

		private Entry(Class<?> to, Class<?> from, int distance, Supplier<O> supplier, BiConsumer<O, I> consumer)
		{
			this.to = to;
			this.from = from;
			this.distance = distance;
			this.supplier = supplier;
			this.consumer = consumer;
		}

		public boolean match(IWulfeniteScript from, Class<?> to)
		{
			return from.getType() == this.from && to == this.to;
		}

		public IWulfeniteScript apply(IWulfeniteScript from)
		{
			O slot = supplier.get();
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
