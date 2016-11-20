package mirrg.application.math.wulfenite.script;

import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.math3.util.FastMath;

import mirrg.application.math.wulfenite.core.types.SlotBoolean;
import mirrg.application.math.wulfenite.core.types.SlotDouble;
import mirrg.application.math.wulfenite.core.types.SlotInteger;
import mirrg.application.math.wulfenite.core.types.SlotString;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.math.hydrogen.complex.functions.Exponential;
import mirrg.helium.math.hydrogen.complex.functions.Trigonometry;

public class Loader
{

	public static void loadEnvironment(Environment environment)
	{
		new Loader(environment);
	}

	private Loader(Environment environment)
	{
		this.environment = environment;

		// 定数
		{
			environment.addVariable("PI", SlotDouble.class).value = new SlotDouble(Math.PI);
			environment.addVariable("E", SlotDouble.class).value = new SlotDouble(Math.E);
			environment.addVariable("true", SlotBoolean.class).value = new SlotBoolean(true);
			environment.addVariable("false", SlotBoolean.class).value = new SlotBoolean(false);
		}

		// 四則演算
		{

			a("_leftPlus", new WsfII((z, a) -> z.value = a.value));
			a("_leftPlus", new WsfDD((z, a) -> z.value = a.value));
			a("_leftPlus", new WsfCC((z, a) -> z.set(a)));
			a("_leftMinus", new WsfII((z, a) -> z.value = -a.value));
			a("_leftMinus", new WsfDD((z, a) -> z.value = -a.value));
			a("_leftMinus", new WsfCC((z, a) -> z.set(-a.re, -a.im)));
			a("_leftTilde", new WsfCC((z, a) -> z.set(a.re, -a.im)));

			a("_operatorPlus", new WsfIII((z, a, b) -> z.value = a.value + b.value));
			a("_operatorPlus", new WsfDDD((z, a, b) -> z.value = a.value + b.value));
			a("_operatorPlus", new WsfCCC((z, a, b) -> {
				z.re = a.re + b.re;
				z.im = a.im + b.im;
			}));
			a("_operatorMinus", new WsfIII((z, a, b) -> z.value = a.value - b.value));
			a("_operatorMinus", new WsfDDD((z, a, b) -> z.value = a.value - b.value));
			a("_operatorMinus", new WsfCCC((z, a, b) -> {
				z.re = a.re - b.re;
				z.im = a.im - b.im;
			}));

			a("_operatorAsterisk", new WsfIII((z, a, b) -> z.value = a.value * b.value));
			a("_operatorAsterisk", new WsfDDD((z, a, b) -> z.value = a.value * b.value));
			a("_operatorAsterisk", new WsfCCC((z, a, b) -> {
				z.set(a);
				z.mul(b);
			}));
			a("_operatorSlash", new WsfIII((z, a, b) -> z.value = a.value / b.value));
			a("_operatorSlash", new WsfDDD((z, a, b) -> z.value = a.value / b.value));
			a("_operatorSlash", new WsfCCC((z, a, b) -> {
				z.set(a);
				z.div(b);
			}));
			a("_operatorPercent", new WsfIII((z, a, b) -> z.value = a.value % b.value));

		}

		// 比較
		{

			a("_operatorLessEqual", new WsfBII((z, a, b) -> z.value = a.value <= b.value));
			a("_operatorLessEqual", new WsfBDD((z, a, b) -> z.value = a.value <= b.value));
			a("_operatorGreaterEqual", new WsfBII((z, a, b) -> z.value = a.value >= b.value));
			a("_operatorGreaterEqual", new WsfBDD((z, a, b) -> z.value = a.value >= b.value));
			a("_operatorLess", new WsfBII((z, a, b) -> z.value = a.value < b.value));
			a("_operatorLess", new WsfBDD((z, a, b) -> z.value = a.value < b.value));
			a("_operatorGreater", new WsfBII((z, a, b) -> z.value = a.value > b.value));
			a("_operatorGreater", new WsfBDD((z, a, b) -> z.value = a.value > b.value));
			a("_operatorEqualEqual", new WsfBII((z, a, b) -> z.value = a.value == b.value));
			a("_operatorEqualEqual", new WsfBDD((z, a, b) -> z.value = a.value == b.value));
			a("_operatorExclamationEqual", new WsfBII((z, a, b) -> z.value = a.value != b.value));
			a("_operatorExclamationEqual", new WsfBDD((z, a, b) -> z.value = a.value != b.value));

		}

		// 論理
		{

			a("_leftExclamation", new WsfBB((z, a) -> z.value = !a.value));

			a("_operatorAmpersandAmpersand", new WsfBBB((z, a, b) -> z.value = a.value && b.value));
			a("_operatorPipePipe", new WsfBBB((z, a, b) -> z.value = a.value || b.value));

		}

		// 文字列
		{

			a("_operatorPlus", new WsfSSS((z, a, b) -> z.value = a.value + b.value));

		}

		// 特殊キャスト
		{

			a("re", new WsfDC((z, a) -> z.value = a.re));
			a("im", new WsfDC((z, a) -> z.value = a.im));
			a("abs", new WsfDC((z, a) -> z.value = a.getAbstract()));
			a("abs2", new WsfDC((z, a) -> z.value = a.getAbstract2()));
			a("arg", new WsfDC((z, a) -> z.value = a.getArgument()));
			a("logabs", new WsfDC((z, a) -> z.value = a.getLogAbstract()));

			a("floor", new WsfID((z, a) -> z.value = (int) FastMath.floor(a.value)));
			a("ceil", new WsfID((z, a) -> z.value = (int) FastMath.ceil(a.value)));
			a("round", new WsfID((z, a) -> z.value = (int) FastMath.round(a.value)));

			a("sign", new WsfID((z, a) -> z.value = (int) FastMath.signum(a.value)));

		}

		// 三角関数
		{

			a("sin", new WsfDD((z, a) -> z.value = FastMath.sin(a.value)));
			a("sin", new WsfCC((z, a) -> {
				z.set(a);
				Trigonometry.sin(z);
			}));
			a("cos", new WsfDD((z, a) -> z.value = FastMath.cos(a.value)));
			a("cos", new WsfCC((z, a) -> {
				z.set(a);
				Trigonometry.cos(z);
			}));
			a("tan", new WsfDD((z, a) -> z.value = FastMath.tan(a.value)));
			a("tan", new WsfCC((z, a) -> {
				z.set(a);
				Trigonometry.tan(z);
			}));

			a("sinh", new WsfDD((z, a) -> z.value = FastMath.sinh(a.value)));
			a("sinh", new WsfCC((z, a) -> {
				z.set(a);
				Trigonometry.sinh(z);
			}));
			a("cosh", new WsfDD((z, a) -> z.value = FastMath.cosh(a.value)));
			a("cosh", new WsfCC((z, a) -> {
				z.set(a);
				Trigonometry.cosh(z);
			}));
			a("tanh", new WsfDD((z, a) -> z.value = FastMath.tanh(a.value)));
			a("tanh", new WsfCC((z, a) -> {
				z.set(a);
				Trigonometry.tanh(z);
			}));

			a("asin", new WsfDD((z, a) -> z.value = FastMath.asin(a.value)));
			a("acos", new WsfDD((z, a) -> z.value = FastMath.acos(a.value)));
			a("atan", new WsfDD((z, a) -> z.value = FastMath.atan(a.value)));
			a("asinh", new WsfDD((z, a) -> z.value = FastMath.asinh(a.value)));
			a("acosh", new WsfDD((z, a) -> z.value = FastMath.acosh(a.value)));
			a("atanh", new WsfDD((z, a) -> z.value = FastMath.atanh(a.value)));

		}

		// 指数関数
		{

			a("pow", new WsfDDI((z, a, b) -> z.value = FastMath.pow(a.value, b.value)));
			alias("_operatorHat");
			a("pow", new WsfDDD((z, a, b) -> z.value = FastMath.pow(a.value, b.value)));
			alias("_operatorHat");
			a("pow", new WsfCCD((z, a, b) -> {
				z.set(a);
				Exponential.pow(z, b.value);
			}));
			alias("_operatorHat");
			a("pow", new WsfCCC((z, a, b) -> {
				z.set(a);
				Exponential.pow(z, b);
			}));
			alias("_operatorHat");

			a("sqrt", new WsfDD((z, a) -> z.value = FastMath.sqrt(a.value)));
			a("sqrt", new WsfCC((z, a) -> {
				double arg = z.getArgument();
				double abs = z.getAbstract();
				z.setPolar(FastMath.sqrt(abs), arg / 2);
			}));

			a("exp", new WsfDD((z, a) -> z.value = FastMath.exp(a.value)));
			a("exp", new WsfCC((z, a) -> {
				z.set(a);
				Exponential.exp(z);
			}));

			a("log", new WsfDD((z, a) -> z.value = FastMath.log(a.value)));
			a("log", new WsfCC((z, a) -> {
				z.set(a);
				Exponential.log(z);
			}));
			a("log", new WsfDDD((z, a, b) -> z.value = FastMath.log(a.value, b.value)));
			a("log", new WsfCCC((z, a, b) -> {
				z.set(a);
				Exponential.log(z, b);
			}));

		}

	}

	private Environment environment;
	private WulfeniteScriptFunction prev;

	private void a(String name, WulfeniteScriptFunction wulfeniteScriptFunction)
	{
		environment.addFunction(name, wulfeniteScriptFunction);
		prev = wulfeniteScriptFunction;
	}

	private void alias(String name)
	{
		environment.addFunction(name, prev);
	}

	private static class Wsf1Arg<Z, A> extends WulfeniteScriptFunction
	{

		private I<Z, A> i;
		private Supplier<Z> supplier;

		public Wsf1Arg(I<Z, A> i, Supplier<Z> supplier, Class<Z> z, Class<A> a)
		{
			super(z, a);
			this.i = i;
			this.supplier = supplier;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Function<Object[], Object> createValueProvider()
		{
			Z slot = supplier.get();
			return args -> {
				i.getValue(slot, (A) args[0]);
				return slot;
			};
		}

		public static interface I<Z, A>
		{

			public void getValue(Z z, A a);

		}

	}

	private static class Wsf2Arg<Z, A, B> extends WulfeniteScriptFunction
	{

		private I<Z, A, B> i;
		private Supplier<Z> supplier;

		public Wsf2Arg(I<Z, A, B> i, Supplier<Z> supplier, Class<Z> z, Class<A> a, Class<B> b)
		{
			super(z, a, b);
			this.i = i;
			this.supplier = supplier;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Function<Object[], Object> createValueProvider()
		{
			Z slot = supplier.get();
			return args -> {
				i.getValue(slot, (A) args[0], (B) args[1]);
				return slot;
			};
		}

		public static interface I<Z, A, B>
		{

			public void getValue(Z z, A a, B b);

		}

	}

	private static class WsfIII extends Wsf2Arg<SlotInteger, SlotInteger, SlotInteger>
	{

		public WsfIII(I<SlotInteger, SlotInteger, SlotInteger> i)
		{
			super(i, SlotInteger::new, SlotInteger.class, SlotInteger.class, SlotInteger.class);
		}

	}

	private static class WsfDDD extends Wsf2Arg<SlotDouble, SlotDouble, SlotDouble>
	{

		public WsfDDD(I<SlotDouble, SlotDouble, SlotDouble> i)
		{
			super(i, SlotDouble::new, SlotDouble.class, SlotDouble.class, SlotDouble.class);
		}

	}

	private static class WsfCCC extends Wsf2Arg<StructureComplex, StructureComplex, StructureComplex>
	{

		public WsfCCC(I<StructureComplex, StructureComplex, StructureComplex> i)
		{
			super(i, StructureComplex::new, StructureComplex.class, StructureComplex.class, StructureComplex.class);
		}

	}

	private static class WsfBBB extends Wsf2Arg<SlotBoolean, SlotBoolean, SlotBoolean>
	{

		public WsfBBB(I<SlotBoolean, SlotBoolean, SlotBoolean> i)
		{
			super(i, SlotBoolean::new, SlotBoolean.class, SlotBoolean.class, SlotBoolean.class);
		}

	}

	private static class WsfSSS extends Wsf2Arg<SlotString, SlotString, SlotString>
	{

		public WsfSSS(I<SlotString, SlotString, SlotString> i)
		{
			super(i, SlotString::new, SlotString.class, SlotString.class, SlotString.class);
		}

	}

	private static class WsfII extends Wsf1Arg<SlotInteger, SlotInteger>
	{

		public WsfII(I<SlotInteger, SlotInteger> i)
		{
			super(i, SlotInteger::new, SlotInteger.class, SlotInteger.class);
		}

	}

	private static class WsfDD extends Wsf1Arg<SlotDouble, SlotDouble>
	{

		public WsfDD(I<SlotDouble, SlotDouble> i)
		{
			super(i, SlotDouble::new, SlotDouble.class, SlotDouble.class);
		}

	}

	private static class WsfCC extends Wsf1Arg<StructureComplex, StructureComplex>
	{

		public WsfCC(I<StructureComplex, StructureComplex> i)
		{
			super(i, StructureComplex::new, StructureComplex.class, StructureComplex.class);
		}

	}

	private static class WsfBB extends Wsf1Arg<SlotBoolean, SlotBoolean>
	{

		public WsfBB(I<SlotBoolean, SlotBoolean> i)
		{
			super(i, SlotBoolean::new, SlotBoolean.class, SlotBoolean.class);
		}

	}

	private static class WsfDC extends Wsf1Arg<SlotDouble, StructureComplex>
	{

		public WsfDC(I<SlotDouble, StructureComplex> i)
		{
			super(i, SlotDouble::new, SlotDouble.class, StructureComplex.class);
		}

	}

	private static class WsfID extends Wsf1Arg<SlotInteger, SlotDouble>
	{

		public WsfID(I<SlotInteger, SlotDouble> i)
		{
			super(i, SlotInteger::new, SlotInteger.class, SlotDouble.class);
		}

	}

	private static class WsfDDI extends Wsf2Arg<SlotDouble, SlotDouble, SlotInteger>
	{

		public WsfDDI(I<SlotDouble, SlotDouble, SlotInteger> i)
		{
			super(i, SlotDouble::new, SlotDouble.class, SlotDouble.class, SlotInteger.class);
		}

	}

	private static class WsfCCD extends Wsf2Arg<StructureComplex, StructureComplex, SlotInteger>
	{

		public WsfCCD(I<StructureComplex, StructureComplex, SlotInteger> i)
		{
			super(i, StructureComplex::new, StructureComplex.class, StructureComplex.class, SlotInteger.class);
		}

	}

	private static class WsfBII extends Wsf2Arg<SlotBoolean, SlotInteger, SlotInteger>
	{

		public WsfBII(I<SlotBoolean, SlotInteger, SlotInteger> i)
		{
			super(i, SlotBoolean::new, SlotBoolean.class, SlotInteger.class, SlotInteger.class);
		}

	}

	private static class WsfBDD extends Wsf2Arg<SlotBoolean, SlotDouble, SlotDouble>
	{

		public WsfBDD(I<SlotBoolean, SlotDouble, SlotDouble> i)
		{
			super(i, SlotBoolean::new, SlotBoolean.class, SlotDouble.class, SlotDouble.class);
		}

	}

}
