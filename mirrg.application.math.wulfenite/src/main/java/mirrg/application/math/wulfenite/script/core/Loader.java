package mirrg.application.math.wulfenite.script.core;

import static mirrg.application.math.wulfenite.core.types.Type.*;

import java.util.function.Function;

import org.apache.commons.math3.util.FastMath;

import mirrg.application.math.wulfenite.core.types.SlotBoolean;
import mirrg.application.math.wulfenite.core.types.SlotDouble;
import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.function.WSFunction;
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
			environment.addVariable("PI", Type.DOUBLE).value = new SlotDouble(Math.PI);
			environment.addVariable("E", Type.DOUBLE).value = new SlotDouble(Math.E);
			environment.addVariable("i", Type.COMPLEX).value = new StructureComplex(0, 1);
			environment.addVariable("true", Type.BOOLEAN).value = new SlotBoolean(true);
			environment.addVariable("false", Type.BOOLEAN).value = new SlotBoolean(false);
		}

		// 四則演算
		{

			a("_leftPlus", new WSF1<>(I, I, (z, a) -> z.value = a.value));
			a("_leftPlus", new WSF1<>(D, D, (z, a) -> z.value = a.value));
			a("_leftPlus", new WSF1<>(C, C, (z, a) -> z.set(a)));
			a("_leftMinus", new WSF1<>(I, I, (z, a) -> z.value = -a.value));
			a("_leftMinus", new WSF1<>(D, D, (z, a) -> z.value = -a.value));
			a("_leftMinus", new WSF1<>(C, C, (z, a) -> z.set(-a.re, -a.im)));
			a("_leftTilde", new WSF1<>(C, C, (z, a) -> z.set(a.re, -a.im)));

			a("_operatorPlus", new WSF2<>(I, I, I, (z, a, b) -> z.value = a.value + b.value));
			a("_operatorPlus", new WSF2<>(D, D, D, (z, a, b) -> z.value = a.value + b.value));
			a("_operatorPlus", new WSF2<>(C, C, C, (z, a, b) -> {
				z.re = a.re + b.re;
				z.im = a.im + b.im;
			}));
			a("_operatorMinus", new WSF2<>(I, I, I, (z, a, b) -> z.value = a.value - b.value));
			a("_operatorMinus", new WSF2<>(D, D, D, (z, a, b) -> z.value = a.value - b.value));
			a("_operatorMinus", new WSF2<>(C, C, C, (z, a, b) -> {
				z.re = a.re - b.re;
				z.im = a.im - b.im;
			}));

			a("_operatorAsterisk", new WSF2<>(I, I, I, (z, a, b) -> z.value = a.value * b.value));
			a("_operatorAsterisk", new WSF2<>(D, D, D, (z, a, b) -> z.value = a.value * b.value));
			a("_operatorAsterisk", new WSF2<>(C, C, C, (z, a, b) -> {
				z.set(a);
				z.mul(b);
			}));
			a("_operatorSlash", new WSF2<>(I, I, I, (z, a, b) -> z.value = a.value / b.value));
			a("_operatorSlash", new WSF2<>(D, D, D, (z, a, b) -> z.value = a.value / b.value));
			a("_operatorSlash", new WSF2<>(C, C, C, (z, a, b) -> {
				z.set(a);
				z.div(b);
			}));
			a("_operatorPercent", new WSF2<>(I, I, I, (z, a, b) -> z.value = a.value % b.value));

		}

		// 比較
		{

			a("_operatorLessEqual", new WSF2<>(B, I, I, (z, a, b) -> z.value = a.value <= b.value));
			a("_operatorLessEqual", new WSF2<>(B, D, D, (z, a, b) -> z.value = a.value <= b.value));
			a("_operatorGreaterEqual", new WSF2<>(B, I, I, (z, a, b) -> z.value = a.value >= b.value));
			a("_operatorGreaterEqual", new WSF2<>(B, D, D, (z, a, b) -> z.value = a.value >= b.value));
			a("_operatorLess", new WSF2<>(B, I, I, (z, a, b) -> z.value = a.value < b.value));
			a("_operatorLess", new WSF2<>(B, D, D, (z, a, b) -> z.value = a.value < b.value));
			a("_operatorGreater", new WSF2<>(B, I, I, (z, a, b) -> z.value = a.value > b.value));
			a("_operatorGreater", new WSF2<>(B, D, D, (z, a, b) -> z.value = a.value > b.value));
			a("_operatorEqualEqual", new WSF2<>(B, I, I, (z, a, b) -> z.value = a.value == b.value));
			a("_operatorEqualEqual", new WSF2<>(B, D, D, (z, a, b) -> z.value = a.value == b.value));
			a("_operatorExclamationEqual", new WSF2<>(B, I, I, (z, a, b) -> z.value = a.value != b.value));
			a("_operatorExclamationEqual", new WSF2<>(B, D, D, (z, a, b) -> z.value = a.value != b.value));

		}

		// 論理
		{

			a("_leftExclamation", new WSF1<>(B, B, (z, a) -> z.value = !a.value));

			a("_operatorAmpersandAmpersand", new WSF2<>(B, B, B, (z, a, b) -> z.value = a.value && b.value));
			a("_operatorPipePipe", new WSF2<>(B, B, B, (z, a, b) -> z.value = a.value || b.value));

		}

		// 条件
		{

			setName("_ternaryQuestionColon");
			a(new WSF3<>(I, B, I, I, (z, a, b, c) -> z.value = a.value ? b.value : c.value));
			a(new WSF3<>(D, B, D, D, (z, a, b, c) -> z.value = a.value ? b.value : c.value));
			a(new WSF3<>(C, B, C, C, (z, a, b, c) -> z.set(a.value ? b : c)));
			a(new WSF3<>(B, B, B, B, (z, a, b, c) -> z.value = a.value ? b.value : c.value));
			a(new WSF3<>(S, B, S, S, (z, a, b, c) -> z.value = a.value ? b.value : c.value));
			a(new WSF3<>(Co, B, Co, Co, (z, a, b, c) -> z.value = a.value ? b.value : c.value));

		}

		// 文字列
		{

			a("_operatorPlus", new WSF2<>(S, S, S, (z, a, b) -> z.value = a.value + b.value));

		}

		// 特殊キャスト
		{

			a("re", new WSF1<>(D, C, (z, a) -> z.value = a.re));
			a("im", new WSF1<>(D, C, (z, a) -> z.value = a.im));
			a("abs", new WSF1<>(D, C, (z, a) -> z.value = a.getAbstract()));
			a("abs2", new WSF1<>(D, C, (z, a) -> z.value = a.getAbstract2()));
			a("arg", new WSF1<>(D, C, (z, a) -> z.value = a.getArgument()));
			a("logabs", new WSF1<>(D, C, (z, a) -> z.value = a.getLogAbstract()));

			a("floor", new WSF1<>(I, D, (z, a) -> z.value = (int) FastMath.floor(a.value)));
			a("ceil", new WSF1<>(I, D, (z, a) -> z.value = (int) FastMath.ceil(a.value)));
			a("round", new WSF1<>(I, D, (z, a) -> z.value = (int) FastMath.round(a.value)));

			a("sign", new WSF1<>(I, D, (z, a) -> z.value = (int) FastMath.signum(a.value)));

		}

		// 三角関数
		{

			a("sin", new WSF1<>(D, D, (z, a) -> z.value = FastMath.sin(a.value)));
			a("sin", new WSF1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.sin(z);
			}));
			a("cos", new WSF1<>(D, D, (z, a) -> z.value = FastMath.cos(a.value)));
			a("cos", new WSF1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.cos(z);
			}));
			a("tan", new WSF1<>(D, D, (z, a) -> z.value = FastMath.tan(a.value)));
			a("tan", new WSF1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.tan(z);
			}));

			a("sinh", new WSF1<>(D, D, (z, a) -> z.value = FastMath.sinh(a.value)));
			a("sinh", new WSF1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.sinh(z);
			}));
			a("cosh", new WSF1<>(D, D, (z, a) -> z.value = FastMath.cosh(a.value)));
			a("cosh", new WSF1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.cosh(z);
			}));
			a("tanh", new WSF1<>(D, D, (z, a) -> z.value = FastMath.tanh(a.value)));
			a("tanh", new WSF1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.tanh(z);
			}));

			a("asin", new WSF1<>(D, D, (z, a) -> z.value = FastMath.asin(a.value)));
			a("acos", new WSF1<>(D, D, (z, a) -> z.value = FastMath.acos(a.value)));
			a("atan", new WSF1<>(D, D, (z, a) -> z.value = FastMath.atan(a.value)));
			a("atan2", new WSF2<>(D, D, D, (z, a, b) -> z.value = FastMath.atan2(a.value, b.value)));
			a("asinh", new WSF1<>(D, D, (z, a) -> z.value = FastMath.asinh(a.value)));
			a("acosh", new WSF1<>(D, D, (z, a) -> z.value = FastMath.acosh(a.value)));
			a("atanh", new WSF1<>(D, D, (z, a) -> z.value = FastMath.atanh(a.value)));

		}

		// 指数関数
		{

			a("pow", new WSF2<>(D, D, I, (z, a, b) -> z.value = FastMath.pow(a.value, b.value)));
			alias("_operatorHat");
			a("pow", new WSF2<>(D, D, D, (z, a, b) -> z.value = FastMath.pow(a.value, b.value)));
			alias("_operatorHat");
			a("pow", new WSF2<>(C, C, D, (z, a, b) -> {
				z.set(a);
				Exponential.pow(z, b.value);
			}));
			alias("_operatorHat");
			a("pow", new WSF2<>(C, C, C, (z, a, b) -> {
				z.set(a);
				Exponential.pow(z, b);
			}));
			alias("_operatorHat");

			a("sqrt", new WSF1<>(D, D, (z, a) -> z.value = FastMath.sqrt(a.value)));
			a("sqrt", new WSF1<>(C, C, (z, a) -> {
				double arg = z.getArgument();
				double abs = z.getAbstract();
				z.setPolar(FastMath.sqrt(abs), arg / 2);
			}));

			a("exp", new WSF1<>(D, D, (z, a) -> z.value = FastMath.exp(a.value)));
			a("exp", new WSF1<>(C, C, (z, a) -> {
				z.set(a);
				Exponential.exp(z);
			}));

			a("log", new WSF1<>(D, D, (z, a) -> z.value = FastMath.log(a.value)));
			a("log", new WSF1<>(C, C, (z, a) -> {
				z.set(a);
				Exponential.log(z);
			}));
			a("log", new WSF2<>(D, D, D, (z, a, b) -> z.value = FastMath.log(a.value, b.value)));
			a("log", new WSF2<>(C, C, C, (z, a, b) -> {
				z.set(a);
				Exponential.log(z, b);
			}));

		}

		// その他
		{

			a("mand", new WSF1<>(I, C, (z, a) -> z.value = mand(0, 0, a.re, a.im, 360 * 4)));
			a("mand", new WSF3<>(I, C, C, I, (z, a, b, lim) -> z.value = mand(a.re, a.im, b.re, b.im, lim.value)));

		}

	}

	private int mand(double a, double b, double c, double d, int lim)
	{
		int t = 0;
		double x2 = a;
		double y2 = b;
		while (t < lim) {
			x2 += c;
			y2 += d;
			double e = x2 * x2;
			double f = y2 * y2;
			if (e + f > 4) break;
			double tmp = e - f;
			y2 = 2 * x2 * y2;
			x2 = tmp;
			t++;
		}
		return t;
	}

	private String name;
	private Environment environment;
	private WSFunction<?> wsFunction;

	private void setName(String name)
	{
		this.name = name;
	}

	private void a(WSFunction<?> wsFunction)
	{
		this.wsFunction = wsFunction;

		environment.addFunction(name, wsFunction);
	}

	private void a(String name, WSFunction<?> wsFunction)
	{
		this.name = name;
		this.wsFunction = wsFunction;

		environment.addFunction(name, wsFunction);
	}

	private void alias(String name)
	{
		environment.addFunction(name, wsFunction);
	}

	@SuppressWarnings("unused")
	private static class WSF0<Z> extends WSFunction<Z>
	{

		private I<Z> i;

		public WSF0(Type<Z> z, I<Z> i)
		{
			super(z);
			this.i = i;
		}

		@Override
		public Function<Object[], Object> createValueProvider()
		{
			Z slot = type.create();
			return args -> {
				i.getValue(slot);
				return slot;
			};
		}

		public static interface I<Z>
		{

			public void getValue(Z z);

		}

	}

	private static class WSF1<Z, A> extends WSFunction<Z>
	{

		private I<Z, A> i;

		public WSF1(Type<Z> z, Type<A> a, I<Z, A> i)
		{
			super(z, a);
			this.i = i;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Function<Object[], Object> createValueProvider()
		{
			Z slot = type.create();
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

	private static class WSF2<Z, A, B> extends WSFunction<Z>
	{

		private I<Z, A, B> i;

		public WSF2(Type<Z> z, Type<A> a, Type<B> b, I<Z, A, B> i)
		{
			super(z, a, b);
			this.i = i;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Function<Object[], Object> createValueProvider()
		{
			Z slot = type.create();
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

	private static class WSF3<Z, A, B, C> extends WSFunction<Z>
	{

		private I<Z, A, B, C> i;

		public WSF3(Type<Z> z, Type<A> a, Type<B> b, Type<C> c, I<Z, A, B, C> i)
		{
			super(z, a, b, c);
			this.i = i;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Function<Object[], Object> createValueProvider()
		{
			Z slot = type.create();
			return args -> {
				i.getValue(slot, (A) args[0], (B) args[1], (C) args[2]);
				return slot;
			};
		}

		public static interface I<Z, A, B, C>
		{

			public void getValue(Z z, A a, B b, C c);

		}

	}

}
