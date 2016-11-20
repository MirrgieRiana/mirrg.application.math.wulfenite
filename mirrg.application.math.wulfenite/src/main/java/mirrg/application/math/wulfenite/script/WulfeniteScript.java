package mirrg.application.math.wulfenite.script;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.application.math.wulfenite.script.nodes.ExpressionRoot;
import mirrg.application.math.wulfenite.script.nodes.LiteralColor;
import mirrg.application.math.wulfenite.script.nodes.LiteralComplex;
import mirrg.application.math.wulfenite.script.nodes.LiteralDouble;
import mirrg.application.math.wulfenite.script.nodes.LiteralInteger;
import mirrg.application.math.wulfenite.script.nodes.LiteralString;
import mirrg.application.math.wulfenite.script.nodes.OperationFunction;
import mirrg.application.math.wulfenite.script.nodes.OperationVariable;
import mirrg.application.math.wulfenite.script.nodes.TokenIdentifier;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxOr;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSlot;
import mirrg.helium.standard.hydrogen.struct.Struct2;
import mirrg.helium.standard.hydrogen.struct.Struct3;

public class WulfeniteScript
{

	public static Syntax<IWulfeniteFormula> getSyntax()
	{
		return new WulfeniteScript().root;
	}

	public Syntax<String> comment = named(regex("[ \t\r\n]*"), "Comment");

	public Syntax<String> identifier = named(regex("[a-zA-Z_][a-zA-Z0-9_]*"), "Identifier");

	public Syntax<String> tokenInteger = named(regex("[0-9]+"), "Integer");

	public Syntax<String> tokenDouble = named(regex("[0-9]+\\.[0-9]+"), "Double");

	public Syntax<String> tokenString = named(pack(extract((ArrayList<String>) null)
		.and(string("\""))
		.extract(repeat(or((String) null)
			.or(pack(regex("\\\\r"), t -> "\r"))
			.or(pack(regex("\\\\n"), t -> "\n"))
			.or(pack(regex("\\\\t"), t -> "\t"))
			.or(pack(regex("\\\\."), t -> "" + t.charAt(1)))
			.or(regex("[^\"]"))))
		.and(string("\"")),
		t -> String.join("", t)), "String");

	public SyntaxSlot<IWulfeniteFormula> expression = slot();

	// factor

	public Syntax<IWulfeniteFormula> literalImaginary = packNode(extract((String) null)
		.extract(or((String) null)
			.or(tokenDouble)
			.or(tokenInteger))
		.and(string("i")),
		n -> new LiteralComplex(n, n.value));
	public Syntax<IWulfeniteFormula> literalDouble = packNode(tokenDouble,
		n -> new LiteralDouble(n, n.value));
	public Syntax<IWulfeniteFormula> literalInteger = packNode(tokenInteger,
		n -> new LiteralInteger(n, n.value));
	public Syntax<IWulfeniteFormula> literalColor = packNode(named(extract((String) null)
		.and(string("#"))
		.extract(regex("[0-9a-fA-F]+")), "Color"),
		n -> new LiteralColor(n, n.value));
	public Syntax<IWulfeniteFormula> literalString = packNode(tokenString,
		n -> new LiteralString(n, n.value));
	public Syntax<IWulfeniteFormula> function = packNode(serial(Struct3<TokenIdentifier, ArrayList<IWulfeniteFormula>, Node<?>>::new)
		.and(pack(identifier, t -> new TokenIdentifier(t)), Struct3::setX)
		.and(comment)
		.and(string("("))
		.and(createArgument(), Struct3::setY)
		.and(packNode(string(")"),
			n -> n), Struct3::setZ),
		n -> {
			IWulfeniteFormula[] args = new IWulfeniteFormula[n.value.y.size()];
			for (int i = 0; i < n.value.y.size(); i++) {
				args[i] = n.value.y.get(i);
			}
			return new OperationFunction(n.begin, n.end, n.value.x, args);
		});
	public Syntax<IWulfeniteFormula> variable = packNode(identifier,
		n -> new OperationVariable(n, n.value));
	public Syntax<IWulfeniteFormula> bracket = extract((IWulfeniteFormula) null)
		.and(string("("))
		.and(comment)
		.extract(expression)
		.and(comment)
		.and(string(")"));

	public Syntax<IWulfeniteFormula> factor = or((IWulfeniteFormula) null)
		.or(literalImaginary)
		.or(literalDouble)
		.or(literalInteger)
		.or(literalColor)
		.or(literalString)
		.or(function)
		.or(variable)
		.or(bracket);

	// operator

	public Syntax<IWulfeniteFormula> operatorMethod = packNode(serial(Struct2<IWulfeniteFormula, ArrayList<Struct3<TokenIdentifier, ArrayList<IWulfeniteFormula>, Node<?>>>>::new)
		.and(factor, Struct2::setX)
		.and(repeat(serial(Struct3<TokenIdentifier, ArrayList<IWulfeniteFormula>, Node<?>>::new)
			.and(comment)
			.and(string("."))
			.and(comment)
			.and(pack(identifier, t -> new TokenIdentifier(t)), Struct3::setX)
			.and(comment)
			.and(string("("))
			.and(createArgument(), Struct3::setY)
			.and(packNode(string(")"),
				n -> n), Struct3::setZ)), Struct2::setY),
		n -> {
			IWulfeniteFormula left = n.value.x;

			for (Struct3<TokenIdentifier, ArrayList<IWulfeniteFormula>, Node<?>> right : n.value.y) {
				IWulfeniteFormula[] args = new IWulfeniteFormula[right.y.size() + 1];
				args[0] = left;
				for (int i = 0; i < right.y.size(); i++) {
					args[i + 1] = right.y.get(i);
				}
				left = new OperationFunction(left.getBegin(), right.z.end, right.x, args);
			}

			return left;
		});

	public Syntax<IWulfeniteFormula> operatorLeft = createLeft(operatorMethod, c -> {
		c.accept("-", "_leftMinus");
		c.accept("+", "_leftPlus");
		c.accept("!", "_leftExclamation");
		c.accept("~", "_leftTilde");
	});

	public Syntax<IWulfeniteFormula> operatorPow = createOperatorRight(operatorLeft, c -> {
		c.accept("^", "_operatorHat");
	});

	public Syntax<IWulfeniteFormula> operatorMul = createOperatorLeft(operatorPow, c -> {
		c.accept("*", "_operatorAsterisk");
		c.accept("/", "_operatorSlash");
		c.accept("%", "_operatorPercent");
	});

	public Syntax<IWulfeniteFormula> operatorAdd = createOperatorLeft(operatorMul, c -> {
		c.accept("+", "_operatorPlus");
		c.accept("-", "_operatorMinus");
	});

	public Syntax<IWulfeniteFormula> operatorCompare = createOperatorCompare(operatorAdd, c -> {
		c.accept("<=", "_operatorLessEqual");
		c.accept(">=", "_operatorGreaterEqual");
		c.accept("<", "_operatorLess");
		c.accept(">", "_operatorGreater");
		c.accept("==", "_operatorEqualEqual");
		c.accept("!=", "_operatorExclamationEqual");
	});

	public Syntax<IWulfeniteFormula> operatorAnd = createOperatorLeft(operatorCompare, c -> {
		c.accept("&&", "_operatorAmpersandAmpersand");
	});

	public Syntax<IWulfeniteFormula> operatorOr = createOperatorLeft(operatorAnd, c -> {
		c.accept("||", "_operatorPipePipe");
	});

	public SyntaxSlot<IWulfeniteFormula> operatorIif = slot();
	{
		operatorIif.syntax = or((IWulfeniteFormula) null)
			.or(pack(serial(Struct3<IWulfeniteFormula, IWulfeniteFormula, IWulfeniteFormula>::new)
				.and(operatorOr, Struct3::setX)
				.and(comment)
				.and(string("?"))
				.and(comment)
				.and(operatorIif, Struct3::setY)
				.and(comment)
				.and(string(":"))
				.and(comment)
				.and(operatorIif, Struct3::setZ),
				t -> new OperationFunction(t.getX().getBegin(), t.getZ().getEnd(), "_ternaryQuestionColon", t.getX(), t.getY(), t.getZ())))
			.or(operatorOr);
	}

	public Syntax<IWulfeniteFormula> formula = operatorIif;

	public Syntax<Struct2<String, IWulfeniteFormula>> lineAssignment = serial(Struct2<String, IWulfeniteFormula>::new)
		.and(identifier, Struct2::setX)
		.and(comment)
		.and(string("="))
		.and(comment)
		.and(formula, Struct2::setY);

	public Syntax<IWulfeniteFormula> lines = packNode(serial(Struct2<ArrayList<Struct2<String, IWulfeniteFormula>>, IWulfeniteFormula>::new)
		.and(repeat(extract((Struct2<String, IWulfeniteFormula>) null)
			.extract(lineAssignment)
			.and(comment)
			.and(string(";"))
			.and(comment)), Struct2::setX)
		.and(formula, Struct2::setY),
		n -> new ExpressionRoot(n, n.value.x, n.value.y));

	// root

	{
		expression.syntax = lines;
	}

	public Syntax<IWulfeniteFormula> root = extract((IWulfeniteFormula) null)
		.and(comment)
		.extract(expression)
		.and(comment)
		.and(named(string(""), "EOF"));

	protected Syntax<IWulfeniteFormula> createLeft(
		Syntax<IWulfeniteFormula> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<ArrayList<Node<String>>, IWulfeniteFormula>::new)
				.and(repeat(extract((Node<String>) null)
					.extract(packNode(tokens, n -> n))
					.and(comment)), Struct2::setX)
				.and(term, Struct2::setY),
			t -> {
				IWulfeniteFormula right = t.y;
				for (Node<String> left : t.x) {
					right = new OperationFunction(left.begin, right.getEnd(), left.value, right);
				}
				return right;
			});
	}

	protected Syntax<IWulfeniteFormula> createOperatorLeft(
		Syntax<IWulfeniteFormula> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<IWulfeniteFormula, ArrayList<Struct2<String, IWulfeniteFormula>>>::new)
				.and(term, Struct2::setX)
				.and(repeat(serial(Struct2<String, IWulfeniteFormula>::new)
					.and(comment)
					.and(tokens, Struct2::setX)
					.and(comment)
					.and(term, Struct2::setY)), Struct2::setY),
			t -> {
				IWulfeniteFormula left = t.x;
				for (Struct2<String, IWulfeniteFormula> right : t.y) {
					left = new OperationFunction(left.getBegin(), right.y.getEnd(), right.x, left, right.y);
				}
				return left;
			});
	}

	protected Syntax<IWulfeniteFormula> createOperatorRight(
		Syntax<IWulfeniteFormula> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<ArrayList<Struct2<IWulfeniteFormula, String>>, IWulfeniteFormula>::new)
				.and(repeat(serial(Struct2<IWulfeniteFormula, String>::new)
					.and(term, Struct2::setX)
					.and(comment)
					.and(tokens, Struct2::setY)
					.and(comment)), Struct2::setX)
				.and(term, Struct2::setY),
			t -> {
				IWulfeniteFormula right = t.y;
				for (Struct2<IWulfeniteFormula, String> left : t.x) {
					right = new OperationFunction(left.x.getBegin(), right.getEnd(), left.y, left.x, right);
				}
				return right;
			});
	}

	protected Syntax<IWulfeniteFormula> createOperatorCompare(
		Syntax<IWulfeniteFormula> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<IWulfeniteFormula, ArrayList<Struct2<String, IWulfeniteFormula>>>::new)
				.and(term, Struct2::setX)
				.and(repeat(serial(Struct2<String, IWulfeniteFormula>::new)
					.and(comment)
					.and(tokens, Struct2::setX)
					.and(comment)
					.and(term, Struct2::setY)), Struct2::setY),
			t -> {
				if (t.y.size() == 0) return t.x;

				ArrayList<IWulfeniteFormula> formulas = new ArrayList<>();
				{
					IWulfeniteFormula left = t.x;
					for (Struct2<String, IWulfeniteFormula> right : t.y) {
						formulas.add(new OperationFunction(left.getBegin(), right.y.getEnd(), right.x, left, right.y));
						left = right.y;
					}
				}
				IWulfeniteFormula left = formulas.get(0);
				for (int i = 1; i < formulas.size(); i++) {
					IWulfeniteFormula right = formulas.get(i);
					left = new OperationFunction(left.getBegin(), right.getEnd(), "_operatorAmpersandAmpersand", left, right);
				}
				return left;
			});
	}

	protected Syntax<ArrayList<IWulfeniteFormula>> createArgument()
	{
		return extract((ArrayList<IWulfeniteFormula>) null)
			.extract(or((ArrayList<IWulfeniteFormula>) null)
				.or(pack(serial(Struct2<IWulfeniteFormula, ArrayList<IWulfeniteFormula>>::new)
					.and(comment)
					.and(expression, Struct2::setX)
					.and(repeat(extract((IWulfeniteFormula) null)
						.and(comment)
						.and(string(","))
						.and(comment)
						.extract(expression)), Struct2::setY),
					t -> Stream.concat(Stream.of(t.x), t.y.stream())
						.collect(Collectors.toCollection(ArrayList::new))))
				.or(pack(extract((IWulfeniteFormula) null)
					.and(comment)
					.extract(expression),
					t -> new ArrayList<>(Arrays.asList(t))))
				.or(pack(string(""),
					t -> new ArrayList<>())))
			.and(comment);
	}

}
