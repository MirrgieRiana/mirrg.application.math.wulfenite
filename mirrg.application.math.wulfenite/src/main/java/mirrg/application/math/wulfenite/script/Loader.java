package mirrg.application.math.wulfenite.script;

import static mirrg.application.math.wulfenite.core.types.Type.*;

import java.util.function.Function;

import org.apache.commons.math3.util.FastMath;

import mirrg.application.math.wulfenite.core.types.SlotBoolean;
import mirrg.application.math.wulfenite.core.types.SlotDouble;
import mirrg.application.math.wulfenite.core.types.Type;
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
			environment.addVariable("true", Type.BOOLEAN).value = new SlotBoolean(true);
			environment.addVariable("false", Type.BOOLEAN).value = new SlotBoolean(false);
		}

		// 四則演算
		{

			a("_leftPlus", new Wsf1<>(I, I, (z, a) -> z.value = a.value));
			a("_leftPlus", new Wsf1<>(D, D, (z, a) -> z.value = a.value));
			a("_leftPlus", new Wsf1<>(C, C, (z, a) -> z.set(a)));
			a("_leftMinus", new Wsf1<>(I, I, (z, a) -> z.value = -a.value));
			a("_leftMinus", new Wsf1<>(D, D, (z, a) -> z.value = -a.value));
			a("_leftMinus", new Wsf1<>(C, C, (z, a) -> z.set(-a.re, -a.im)));
			a("_leftTilde", new Wsf1<>(C, C, (z, a) -> z.set(a.re, -a.im)));

			a("_operatorPlus", new Wsf2<>(I, I, I, (z, a, b) -> z.value = a.value + b.value));
			a("_operatorPlus", new Wsf2<>(D, D, D, (z, a, b) -> z.value = a.value + b.value));
			a("_operatorPlus", new Wsf2<>(C, C, C, (z, a, b) -> {
				z.re = a.re + b.re;
				z.im = a.im + b.im;
			}));
			a("_operatorMinus", new Wsf2<>(I, I, I, (z, a, b) -> z.value = a.value - b.value));
			a("_operatorMinus", new Wsf2<>(D, D, D, (z, a, b) -> z.value = a.value - b.value));
			a("_operatorMinus", new Wsf2<>(C, C, C, (z, a, b) -> {
				z.re = a.re - b.re;
				z.im = a.im - b.im;
			}));

			a("_operatorAsterisk", new Wsf2<>(I, I, I, (z, a, b) -> z.value = a.value * b.value));
			a("_operatorAsterisk", new Wsf2<>(D, D, D, (z, a, b) -> z.value = a.value * b.value));
			a("_operatorAsterisk", new Wsf2<>(C, C, C, (z, a, b) -> {
				z.set(a);
				z.mul(b);
			}));
			a("_operatorSlash", new Wsf2<>(I, I, I, (z, a, b) -> z.value = a.value / b.value));
			a("_operatorSlash", new Wsf2<>(D, D, D, (z, a, b) -> z.value = a.value / b.value));
			a("_operatorSlash", new Wsf2<>(C, C, C, (z, a, b) -> {
				z.set(a);
				z.div(b);
			}));
			a("_operatorPercent", new Wsf2<>(I, I, I, (z, a, b) -> z.value = a.value % b.value));

		}

		// 比較
		{

			a("_operatorLessEqual", new Wsf2<>(B, I, I, (z, a, b) -> z.value = a.value <= b.value));
			a("_operatorLessEqual", new Wsf2<>(B, D, D, (z, a, b) -> z.value = a.value <= b.value));
			a("_operatorGreaterEqual", new Wsf2<>(B, I, I, (z, a, b) -> z.value = a.value >= b.value));
			a("_operatorGreaterEqual", new Wsf2<>(B, D, D, (z, a, b) -> z.value = a.value >= b.value));
			a("_operatorLess", new Wsf2<>(B, I, I, (z, a, b) -> z.value = a.value < b.value));
			a("_operatorLess", new Wsf2<>(B, D, D, (z, a, b) -> z.value = a.value < b.value));
			a("_operatorGreater", new Wsf2<>(B, I, I, (z, a, b) -> z.value = a.value > b.value));
			a("_operatorGreater", new Wsf2<>(B, D, D, (z, a, b) -> z.value = a.value > b.value));
			a("_operatorEqualEqual", new Wsf2<>(B, I, I, (z, a, b) -> z.value = a.value == b.value));
			a("_operatorEqualEqual", new Wsf2<>(B, D, D, (z, a, b) -> z.value = a.value == b.value));
			a("_operatorExclamationEqual", new Wsf2<>(B, I, I, (z, a, b) -> z.value = a.value != b.value));
			a("_operatorExclamationEqual", new Wsf2<>(B, D, D, (z, a, b) -> z.value = a.value != b.value));

		}

		// 論理
		{

			a("_leftExclamation", new Wsf1<>(B, B, (z, a) -> z.value = !a.value));

			a("_operatorAmpersandAmpersand", new Wsf2<>(B, B, B, (z, a, b) -> z.value = a.value && b.value));
			a("_operatorPipePipe", new Wsf2<>(B, B, B, (z, a, b) -> z.value = a.value || b.value));

		}

		// 条件
		{

			a("_ternaryQuestionColon", new Wsf3<>(I, B, I, I, (z, a, b, c) -> z.value = a.value ? b.value : c.value));
			a("_ternaryQuestionColon", new Wsf3<>(D, B, D, D, (z, a, b, c) -> z.value = a.value ? b.value : c.value));
			a("_ternaryQuestionColon", new Wsf3<>(C, B, C, C, (z, a, b, c) -> z.set(a.value ? b : c)));
			a("_ternaryQuestionColon", new Wsf3<>(B, B, B, B, (z, a, b, c) -> z.value = a.value ? b.value : c.value));
			a("_ternaryQuestionColon", new Wsf3<>(S, B, S, S, (z, a, b, c) -> z.value = a.value ? b.value : c.value));
			a("_ternaryQuestionColon", new Wsf3<>(Co, B, Co, Co, (z, a, b, c) -> z.value = a.value ? b.value : c.value));

		}

		// 文字列
		{

			a("_operatorPlus", new Wsf2<>(S, S, S, (z, a, b) -> z.value = a.value + b.value));

		}

		// 特殊キャスト
		{

			a("re", new Wsf1<>(D, C, (z, a) -> z.value = a.re));
			a("im", new Wsf1<>(D, C, (z, a) -> z.value = a.im));
			a("abs", new Wsf1<>(D, C, (z, a) -> z.value = a.getAbstract()));
			a("abs2", new Wsf1<>(D, C, (z, a) -> z.value = a.getAbstract2()));
			a("arg", new Wsf1<>(D, C, (z, a) -> z.value = a.getArgument()));
			a("logabs", new Wsf1<>(D, C, (z, a) -> z.value = a.getLogAbstract()));

			a("floor", new Wsf1<>(I, D, (z, a) -> z.value = (int) FastMath.floor(a.value)));
			a("ceil", new Wsf1<>(I, D, (z, a) -> z.value = (int) FastMath.ceil(a.value)));
			a("round", new Wsf1<>(I, D, (z, a) -> z.value = (int) FastMath.round(a.value)));

			a("sign", new Wsf1<>(I, D, (z, a) -> z.value = (int) FastMath.signum(a.value)));

		}

		// 三角関数
		{

			a("sin", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.sin(a.value)));
			a("sin", new Wsf1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.sin(z);
			}));
			a("cos", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.cos(a.value)));
			a("cos", new Wsf1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.cos(z);
			}));
			a("tan", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.tan(a.value)));
			a("tan", new Wsf1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.tan(z);
			}));

			a("sinh", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.sinh(a.value)));
			a("sinh", new Wsf1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.sinh(z);
			}));
			a("cosh", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.cosh(a.value)));
			a("cosh", new Wsf1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.cosh(z);
			}));
			a("tanh", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.tanh(a.value)));
			a("tanh", new Wsf1<>(C, C, (z, a) -> {
				z.set(a);
				Trigonometry.tanh(z);
			}));

			a("asin", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.asin(a.value)));
			a("acos", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.acos(a.value)));
			a("atan", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.atan(a.value)));
			a("asinh", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.asinh(a.value)));
			a("acosh", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.acosh(a.value)));
			a("atanh", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.atanh(a.value)));

		}

		// 指数関数
		{

			a("pow", new Wsf2<>(D, D, I, (z, a, b) -> z.value = FastMath.pow(a.value, b.value)));
			alias("_operatorHat");
			a("pow", new Wsf2<>(D, D, D, (z, a, b) -> z.value = FastMath.pow(a.value, b.value)));
			alias("_operatorHat");
			a("pow", new Wsf2<>(C, C, D, (z, a, b) -> {
				z.set(a);
				Exponential.pow(z, b.value);
			}));
			alias("_operatorHat");
			a("pow", new Wsf2<>(C, C, C, (z, a, b) -> {
				z.set(a);
				Exponential.pow(z, b);
			}));
			alias("_operatorHat");

			a("sqrt", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.sqrt(a.value)));
			a("sqrt", new Wsf1<>(C, C, (z, a) -> {
				double arg = z.getArgument();
				double abs = z.getAbstract();
				z.setPolar(FastMath.sqrt(abs), arg / 2);
			}));

			a("exp", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.exp(a.value)));
			a("exp", new Wsf1<>(C, C, (z, a) -> {
				z.set(a);
				Exponential.exp(z);
			}));

			a("log", new Wsf1<>(D, D, (z, a) -> z.value = FastMath.log(a.value)));
			a("log", new Wsf1<>(C, C, (z, a) -> {
				z.set(a);
				Exponential.log(z);
			}));
			a("log", new Wsf2<>(D, D, D, (z, a, b) -> z.value = FastMath.log(a.value, b.value)));
			a("log", new Wsf2<>(C, C, C, (z, a, b) -> {
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

	@SuppressWarnings("unused")
	private static class Wsf0<Z> extends WulfeniteScriptFunction
	{

		private I<Z> i;

		public Wsf0(Type<Z> z, I<Z> i)
		{
			super(z);
			this.i = i;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Function<Object[], Object> createValueProvider()
		{
			Z slot = (Z) type.supplier.get();
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

	private static class Wsf1<Z, A> extends WulfeniteScriptFunction
	{

		private I<Z, A> i;

		public Wsf1(Type<Z> z, Type<A> a, I<Z, A> i)
		{
			super(z, a);
			this.i = i;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Function<Object[], Object> createValueProvider()
		{
			Z slot = (Z) type.supplier.get();
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

	private static class Wsf2<Z, A, B> extends WulfeniteScriptFunction
	{

		private I<Z, A, B> i;

		public Wsf2(Type<Z> z, Type<A> a, Type<B> b, I<Z, A, B> i)
		{
			super(z, a, b);
			this.i = i;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Function<Object[], Object> createValueProvider()
		{
			Z slot = (Z) type.supplier.get();
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

	private static class Wsf3<Z, A, B, C> extends WulfeniteScriptFunction
	{

		private I<Z, A, B, C> i;

		public Wsf3(Type<Z> z, Type<A> a, Type<B> b, Type<C> c, I<Z, A, B, C> i)
		{
			super(z, a, b);
			this.i = i;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Function<Object[], Object> createValueProvider()
		{
			Z slot = (Z) type.supplier.get();
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
