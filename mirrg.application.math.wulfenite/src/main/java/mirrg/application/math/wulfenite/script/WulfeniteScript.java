package mirrg.application.math.wulfenite.script;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public static Syntax<IWulfeniteScript> getSyntax()
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

	public SyntaxSlot<IWulfeniteScript> expression = slot();

	// factor

	public Syntax<IWulfeniteScript> literalImaginary = packNode(extract((String) null)
		.extract(or((String) null)
			.or(tokenDouble)
			.or(tokenInteger))
		.and(string("i")),
		n -> new LiteralComplex(n, n.value));
	public Syntax<IWulfeniteScript> literalDouble = packNode(tokenDouble,
		n -> new LiteralDouble(n, n.value));
	public Syntax<IWulfeniteScript> literalInteger = packNode(tokenInteger,
		n -> new LiteralInteger(n, n.value));
	public Syntax<IWulfeniteScript> literalColor = packNode(named(extract((String) null)
		.and(string("#"))
		.extract(regex("[0-9a-fA-F]+")), "Color"),
		n -> new LiteralColor(n, n.value));
	public Syntax<IWulfeniteScript> literalString = packNode(tokenString,
		n -> new LiteralString(n, n.value));
	public Syntax<IWulfeniteScript> function = packNode(serial(Struct3<TokenIdentifier, ArrayList<IWulfeniteScript>, Node<?>>::new)
		.and(pack(identifier, t -> new TokenIdentifier(t)), Struct3::setX)
		.and(comment)
		.and(string("("))
		.and(createArgument(), Struct3::setY)
		.and(packNode(string(")"),
			n -> n), Struct3::setZ),
		n -> {
			IWulfeniteScript[] args = new IWulfeniteScript[n.value.y.size()];
			for (int i = 0; i < n.value.y.size(); i++) {
				args[i] = n.value.y.get(i);
			}
			return new OperationFunction(n.begin, n.end, n.value.x, args);
		});
	public Syntax<IWulfeniteScript> variable = packNode(identifier,
		n -> new OperationVariable(n, n.value));
	public Syntax<IWulfeniteScript> bracket = extract((IWulfeniteScript) null)
		.and(string("("))
		.and(comment)
		.extract(expression)
		.and(comment)
		.and(string(")"));

	public Syntax<IWulfeniteScript> factor = or((IWulfeniteScript) null)
		.or(literalImaginary)
		.or(literalDouble)
		.or(literalInteger)
		.or(literalColor)
		.or(literalString)
		.or(function)
		.or(variable)
		.or(bracket);

	// operator

	public Syntax<IWulfeniteScript> operatorMethod = packNode(serial(Struct2<IWulfeniteScript, ArrayList<Struct3<TokenIdentifier, ArrayList<IWulfeniteScript>, Node<?>>>>::new)
		.and(factor, Struct2::setX)
		.and(repeat(serial(Struct3<TokenIdentifier, ArrayList<IWulfeniteScript>, Node<?>>::new)
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
			IWulfeniteScript left = n.value.x;

			for (Struct3<TokenIdentifier, ArrayList<IWulfeniteScript>, Node<?>> right : n.value.y) {
				IWulfeniteScript[] args = new IWulfeniteScript[right.y.size() + 1];
				args[0] = left;
				for (int i = 0; i < right.y.size(); i++) {
					args[i + 1] = right.y.get(i);
				}
				left = new OperationFunction(left.getBegin(), right.z.end, right.x, args);
			}

			return left;
		});

	public Syntax<IWulfeniteScript> operatorLeft = createLeft(operatorMethod, c -> {
		c.accept("-", "_leftMinus");
		c.accept("+", "_leftPlus");
		c.accept("!", "_leftExclamation");
		c.accept("~", "_leftTilde");
	});

	public Syntax<IWulfeniteScript> operatorPow = createOperatorRight(operatorLeft, c -> {
		c.accept("^", "_operatorHat");
	});

	public Syntax<IWulfeniteScript> operatorMul = createOperatorLeft(operatorPow, c -> {
		c.accept("*", "_operatorAsterisk");
		c.accept("/", "_operatorSlash");
		c.accept("%", "_operatorPercent");
	});

	public Syntax<IWulfeniteScript> operatorAdd = createOperatorLeft(operatorMul, c -> {
		c.accept("+", "_operatorPlus");
		c.accept("-", "_operatorMinus");
	});

	public Syntax<IWulfeniteScript> operatorCompare = createOperatorCompare(operatorAdd, c -> {
		c.accept("<=", "_operatorLessEqual");
		c.accept(">=", "_operatorGreaterEqual");
		c.accept("<", "_operatorLess");
		c.accept(">", "_operatorGreater");
		c.accept("==", "_operatorEqualEqual");
		c.accept("!=", "_operatorExclamationEqual");
	});

	public Syntax<IWulfeniteScript> operatorAnd = createOperatorLeft(operatorCompare, c -> {
		c.accept("&&", "_operatorAmpersandAmpersand");
	});

	public Syntax<IWulfeniteScript> operatorOr = createOperatorLeft(operatorAnd, c -> {
		c.accept("||", "_operatorPipePipe");
	});

	public SyntaxSlot<IWulfeniteScript> operatorIif = slot();
	{
		operatorIif.syntax = or((IWulfeniteScript) null)
			.or(operatorOr)
			.or(pack(serial(Struct3<IWulfeniteScript, IWulfeniteScript, IWulfeniteScript>::new)
				.and(operatorOr, Struct3::setX)
				.and(comment)
				.and(string("?"))
				.and(comment)
				.and(operatorIif, Struct3::setY)
				.and(comment)
				.and(string(":"))
				.and(comment)
				.and(operatorIif, Struct3::setZ),
				t -> new OperationFunction(t.getX().getBegin(), t.getZ().getEnd(), "_ternaryQuestionColon", t.getX(), t.getY(), t.getZ())));
	}

	// root

	{
		expression.syntax = operatorIif;
	}

	public Syntax<IWulfeniteScript> root = extract((IWulfeniteScript) null)
		.and(comment)
		.extract(expression)
		.and(comment)
		.and(named(string(""), "EOF"));

	protected Syntax<IWulfeniteScript> createLeft(
		Syntax<IWulfeniteScript> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<ArrayList<Node<String>>, IWulfeniteScript>::new)
				.and(repeat(extract((Node<String>) null)
					.extract(packNode(tokens, n -> n))
					.and(comment)), Struct2::setX)
				.and(term, Struct2::setY),
			t -> {
				IWulfeniteScript right = t.y;
				for (Node<String> left : t.x) {
					right = new OperationFunction(left.begin, right.getEnd(), left.value, right);
				}
				return right;
			});
	}

	protected Syntax<IWulfeniteScript> createOperatorLeft(
		Syntax<IWulfeniteScript> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<IWulfeniteScript, ArrayList<Struct2<String, IWulfeniteScript>>>::new)
				.and(term, Struct2::setX)
				.and(repeat(serial(Struct2<String, IWulfeniteScript>::new)
					.and(comment)
					.and(tokens, Struct2::setX)
					.and(comment)
					.and(term, Struct2::setY)), Struct2::setY),
			t -> {
				IWulfeniteScript left = t.x;
				for (Struct2<String, IWulfeniteScript> right : t.y) {
					left = new OperationFunction(left.getBegin(), right.y.getEnd(), right.x, left, right.y);
				}
				return left;
			});
	}

	protected Syntax<IWulfeniteScript> createOperatorRight(
		Syntax<IWulfeniteScript> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<ArrayList<Struct2<IWulfeniteScript, String>>, IWulfeniteScript>::new)
				.and(repeat(serial(Struct2<IWulfeniteScript, String>::new)
					.and(term, Struct2::setX)
					.and(comment)
					.and(tokens, Struct2::setY)
					.and(comment)), Struct2::setX)
				.and(term, Struct2::setY),
			t -> {
				IWulfeniteScript right = t.y;
				for (Struct2<IWulfeniteScript, String> left : t.x) {
					right = new OperationFunction(left.x.getBegin(), right.getEnd(), left.y, left.x, right);
				}
				return right;
			});
	}

	protected Syntax<IWulfeniteScript> createOperatorCompare(
		Syntax<IWulfeniteScript> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<IWulfeniteScript, ArrayList<Struct2<String, IWulfeniteScript>>>::new)
				.and(term, Struct2::setX)
				.and(repeat(serial(Struct2<String, IWulfeniteScript>::new)
					.and(comment)
					.and(tokens, Struct2::setX)
					.and(comment)
					.and(term, Struct2::setY)), Struct2::setY),
			t -> {
				if (t.y.size() == 0) return t.x;

				ArrayList<IWulfeniteScript> wulfeniteScripts = new ArrayList<>();
				{
					IWulfeniteScript left = t.x;
					for (Struct2<String, IWulfeniteScript> right : t.y) {
						wulfeniteScripts.add(new OperationFunction(left.getBegin(), right.y.getEnd(), right.x, left, right.y));
						left = right.y;
					}
				}
				IWulfeniteScript left = wulfeniteScripts.get(0);
				for (int i = 1; i < wulfeniteScripts.size(); i++) {
					IWulfeniteScript right = wulfeniteScripts.get(i);
					left = new OperationFunction(left.getBegin(), right.getEnd(), "_operatorAmpersandAmpersand", left, right);
				}
				return left;
			});
	}

	protected Syntax<ArrayList<IWulfeniteScript>> createArgument()
	{
		return extract((ArrayList<IWulfeniteScript>) null)
			.extract(or((ArrayList<IWulfeniteScript>) null)
				.or(pack(serial(Struct2<IWulfeniteScript, ArrayList<IWulfeniteScript>>::new)
					.and(comment)
					.and(expression, Struct2::setX)
					.and(repeat(extract((IWulfeniteScript) null)
						.and(comment)
						.and(string(","))
						.and(comment)
						.extract(expression)), Struct2::setY),
					t -> Stream.concat(Stream.of(t.x), t.y.stream())
						.collect(Collectors.toCollection(ArrayList::new))))
				.or(pack(extract((IWulfeniteScript) null)
					.and(comment)
					.extract(expression),
					t -> new ArrayList<>(Arrays.asList(t))))
				.or(pack(string(""),
					t -> new ArrayList<>())))
			.and(comment);
	}

}
