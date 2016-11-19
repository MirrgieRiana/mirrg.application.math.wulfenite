package mirrg.application.math.wulfenite.script;

import java.util.function.Function;

import mirrg.application.math.wulfenite.core.SlotDouble;
import mirrg.application.math.wulfenite.core.SlotInteger;
import mirrg.helium.math.hydrogen.complex.StructureComplex;

public class Loader
{

	public static void loadFunction(Environment e)
	{
		e.addFunction(new WsfIII("_operatorPlus", (z, a, b) -> z.value = a.value + b.value));
		e.addFunction(new WsfDDD("_operatorPlus", (z, a, b) -> z.value = a.value + b.value));
		e.addFunction(new WsfCCC("_operatorPlus", (z, a, b) -> {
			z.re = a.re + b.re;
			z.im = a.im + b.im;
		}));
		e.addFunction(new WsfIII("_operatorMinus", (z, a, b) -> z.value = a.value - b.value));
		e.addFunction(new WsfDDD("_operatorMinus", (z, a, b) -> z.value = a.value - b.value));
		e.addFunction(new WsfCCC("_operatorMinus", (z, a, b) -> {
			z.re = a.re - b.re;
			z.im = a.im - b.im;
		}));
		e.addFunction(new WsfIII("_operatorAsterisk", (z, a, b) -> z.value = a.value * b.value));
		e.addFunction(new WsfDDD("_operatorAsterisk", (z, a, b) -> z.value = a.value * b.value));
		e.addFunction(new WsfCCC("_operatorAsterisk", (z, a, b) -> {
			z.set(a);
			z.mul(b);
		}));
		e.addFunction(new WsfIII("_operatorSlash", (z, a, b) -> z.value = a.value / b.value));
		e.addFunction(new WsfDDD("_operatorSlash", (z, a, b) -> z.value = a.value / b.value));
		e.addFunction(new WsfCCC("_operatorSlash", (z, a, b) -> {
			z.set(a);
			z.div(b);
		}));
	}

	private static class WsfIII extends WulfeniteScriptFunction
	{

		private I i;

		public WsfIII(String name, I i)
		{
			super(name, SlotInteger.class, SlotInteger.class, SlotInteger.class);
			this.i = i;
		}

		@Override
		public Function<Object[], Object> createValueProvider()
		{
			SlotInteger slot = new SlotInteger();
			return args -> {
				i.getValue(slot, (SlotInteger) args[0], (SlotInteger) args[1]);
				return slot;
			};
		}

		public static interface I
		{

			public void getValue(SlotInteger z, SlotInteger a, SlotInteger b);

		}

	}

	private static class WsfDDD extends WulfeniteScriptFunction
	{

		private I i;

		public WsfDDD(String name, I i)
		{
			super(name, SlotDouble.class, SlotDouble.class, SlotDouble.class);
			this.i = i;
		}

		@Override
		public Function<Object[], Object> createValueProvider()
		{
			SlotDouble slot = new SlotDouble();
			return args -> {
				i.getValue(slot, (SlotDouble) args[0], (SlotDouble) args[1]);
				return slot;
			};
		}

		public static interface I
		{

			public void getValue(SlotDouble z, SlotDouble a, SlotDouble b);

		}

	}

	private static class WsfCCC extends WulfeniteScriptFunction
	{

		private I i;

		public WsfCCC(String name, I i)
		{
			super(name, StructureComplex.class, StructureComplex.class, StructureComplex.class);
			this.i = i;
		}

		@Override
		public Function<Object[], Object> createValueProvider()
		{
			StructureComplex slot = new StructureComplex();
			return args -> {
				i.getValue(slot, (StructureComplex) args[0], (StructureComplex) args[1]);
				return slot;
			};
		}

		public static interface I
		{

			public void getValue(StructureComplex z, StructureComplex a, StructureComplex b);

		}

	}

}
