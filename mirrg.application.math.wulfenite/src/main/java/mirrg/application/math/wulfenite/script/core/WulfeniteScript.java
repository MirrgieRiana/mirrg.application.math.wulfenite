package mirrg.application.math.wulfenite.script.core;

import static mirrg.helium.compile.oxygen.parser.HSyntaxOxygen.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.application.math.wulfenite.script.node.IWSLine;
import mirrg.application.math.wulfenite.script.nodes.ExpressionRoot;
import mirrg.application.math.wulfenite.script.nodes.LineAssignment;
import mirrg.application.math.wulfenite.script.nodes.LineBrackets;
import mirrg.application.math.wulfenite.script.nodes.LineDefineVariable;
import mirrg.application.math.wulfenite.script.nodes.LineDefineVariableAssignment;
import mirrg.application.math.wulfenite.script.nodes.LineIf;
import mirrg.application.math.wulfenite.script.nodes.LineStatic;
import mirrg.application.math.wulfenite.script.nodes.LineVoid;
import mirrg.application.math.wulfenite.script.nodes.LiteralColor;
import mirrg.application.math.wulfenite.script.nodes.LiteralComplex;
import mirrg.application.math.wulfenite.script.nodes.LiteralDouble;
import mirrg.application.math.wulfenite.script.nodes.LiteralInteger;
import mirrg.application.math.wulfenite.script.nodes.LiteralString;
import mirrg.application.math.wulfenite.script.nodes.OperationCast;
import mirrg.application.math.wulfenite.script.nodes.OperationFunction;
import mirrg.application.math.wulfenite.script.nodes.OperationVariable;
import mirrg.application.math.wulfenite.script.nodes.TokenIdentifier;
import mirrg.helium.compile.oxygen.editor.WithColor;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.compile.oxygen.parser.core.Syntax;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxOr;
import mirrg.helium.compile.oxygen.parser.syntaxes.SyntaxSlot;
import mirrg.helium.standard.hydrogen.struct.Struct2;
import mirrg.helium.standard.hydrogen.struct.Struct3;

public class WulfeniteScript
{

	public static Syntax<IWSFormula> getSyntax()
	{
		return new WulfeniteScript().root;
	}

	public SyntaxSlot<IWSFormula> formula = slot();

	//

	public Syntax<String> comment = named(WithColor.withColor(pack(repeat(or((String) null)
		.or(regex("[ \t\r\n]+"))
		.or(regex("//[^\\r\\n]*"))
		.or(regex("/\\*((?!\\*/)(?s:.))*\\*/"))),
		t -> String.join("", t)),
		t -> Color.decode("#008800")), "Comment");

	public Syntax<String> identifier = named(regex("[a-zA-Z_][a-zA-Z0-9_]*\\b"), "Identifier");

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

	// factor

	public Syntax<IWSFormula> literalImaginary = packNode(extract((String) null)
		.extract(or((String) null)
			.or(tokenDouble)
			.or(tokenInteger))
		.and(string("i")),
		n -> new LiteralComplex(n, n.value));
	public Syntax<IWSFormula> literalDouble = packNode(tokenDouble,
		n -> new LiteralDouble(n, n.value));
	public Syntax<IWSFormula> literalInteger = packNode(tokenInteger,
		n -> new LiteralInteger(n, n.value));
	public Syntax<IWSFormula> literalColor = packNode(named(extract((String) null)
		.and(string("#"))
		.extract(regex("[0-9a-fA-F]+\\b")), "Color"),
		n -> new LiteralColor(n, n.value));
	public Syntax<IWSFormula> literalString = packNode(tokenString,
		n -> new LiteralString(n, n.value));
	public Syntax<IWSFormula> function = packNode(serial(Struct3<TokenIdentifier, ArrayList<IWSFormula>, Node<?>>::new)
		.and(pack(identifier, t -> new TokenIdentifier(t)), Struct3::setX)
		.and(comment)
		.and(string("("))
		.and(createArray(formula, string(",")), Struct3::setY)
		.and(comment)
		.and(packNode(string(")"),
			n -> n), Struct3::setZ),
		n -> {
			IWSFormula[] args = new IWSFormula[n.value.y.size()];
			for (int i = 0; i < n.value.y.size(); i++) {
				args[i] = n.value.y.get(i);
			}
			return new OperationFunction(n.begin, n.end, n.value.x, args);
		});
	public Syntax<IWSFormula> variable = packNode(identifier,
		n -> new OperationVariable(n, n.value));
	public Syntax<IWSFormula> brackets = extract((IWSFormula) null)
		.and(string("("))
		.and(comment)
		.extract(formula)
		.and(comment)
		.and(string(")"));

	public Syntax<IWSFormula> factor = or((IWSFormula) null)
		.or(literalImaginary)
		.or(literalDouble)
		.or(literalInteger)
		.or(literalColor)
		.or(literalString)
		.or(function)
		.or(variable)
		.or(brackets);

	// operator

	public Syntax<IWSFormula> operatorMethod = packNode(serial(Struct2<IWSFormula, ArrayList<Struct3<TokenIdentifier, ArrayList<IWSFormula>, Node<?>>>>::new)
		.and(factor, Struct2::setX)
		.and(repeat(serial(Struct3<TokenIdentifier, ArrayList<IWSFormula>, Node<?>>::new)
			.and(comment)
			.and(string("."))
			.and(comment)
			.and(pack(identifier, t -> new TokenIdentifier(t)), Struct3::setX)
			.and(comment)
			.and(string("("))
			.and(createArray(formula, string(",")), Struct3::setY)
			.and(comment)
			.and(packNode(string(")"),
				n -> n), Struct3::setZ)), Struct2::setY),
		n -> {
			IWSFormula left = n.value.x;

			for (Struct3<TokenIdentifier, ArrayList<IWSFormula>, Node<?>> right : n.value.y) {
				IWSFormula[] args = new IWSFormula[right.y.size() + 1];
				args[0] = left;
				for (int i = 0; i < right.y.size(); i++) {
					args[i + 1] = right.y.get(i);
				}
				left = new OperationFunction(left.getBegin(), right.z.end, right.x, args);
			}

			return left;
		});

	public Syntax<IWSFormula> operatorLeft = createLeft(operatorMethod, c -> {
		c.accept(packNode(string("-"), n -> f -> new OperationFunction(n.begin, f.getEnd(), "_leftMinus", f)));
		c.accept(packNode(string("+"), n -> f -> new OperationFunction(n.begin, f.getEnd(), "_leftPlus", f)));
		c.accept(packNode(string("!"), n -> f -> new OperationFunction(n.begin, f.getEnd(), "_leftExclamation", f)));
		c.accept(packNode(string("~"), n -> f -> new OperationFunction(n.begin, f.getEnd(), "_leftTilde", f)));
		c.accept(packNode(serial(Struct2<Node<?>, String>::new)
			.and(packNode(string("("), n -> n), Struct2::setX)
			.and(comment)
			.and(identifier, Struct2::setY)
			.and(comment)
			.and(string(")")), n -> f -> new OperationCast(n.begin, f.getEnd(), n.value.y, f)));
	});

	public Syntax<IWSFormula> operatorPow = createOperatorRight(operatorLeft, c -> {
		c.accept("^", "_operatorHat");
	});

	public Syntax<IWSFormula> operatorMul = createOperatorLeft(operatorPow, c -> {
		c.accept("*", "_operatorAsterisk");
		c.accept("/", "_operatorSlash");
		c.accept("%", "_operatorPercent");
	});

	public Syntax<IWSFormula> operatorAdd = createOperatorLeft(operatorMul, c -> {
		c.accept("+", "_operatorPlus");
		c.accept("-", "_operatorMinus");
	});

	public Syntax<IWSFormula> operatorCompare = createOperatorCompare(operatorAdd, c -> {
		c.accept("<=", "_operatorLessEqual");
		c.accept(">=", "_operatorGreaterEqual");
		c.accept("<", "_operatorLess");
		c.accept(">", "_operatorGreater");
		c.accept("==", "_operatorEqualEqual");
		c.accept("!=", "_operatorExclamationEqual");
	});

	public Syntax<IWSFormula> operatorAnd = createOperatorLeft(operatorCompare, c -> {
		c.accept("&&", "_operatorAmpersandAmpersand");
	});

	public Syntax<IWSFormula> operatorOr = createOperatorLeft(operatorAnd, c -> {
		c.accept("||", "_operatorPipePipe");
	});

	public SyntaxSlot<IWSFormula> operatorIif = slot();
	{
		operatorIif.syntax = or((IWSFormula) null)
			.or(pack(serial(Struct3<IWSFormula, IWSFormula, IWSFormula>::new)
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

	// lines

	{
		formula.syntax = operatorIif;
	}

	public SyntaxSlot<IWSLine> line = slot();

	public Syntax<IWSLine> lineIf = or((IWSLine) null)
		.or(packNode(serial(Struct3<IWSFormula, IWSLine, IWSLine>::new)
			.and(WithColor.withColor(named(regex("if\\b"), "if"), t -> Color.gray))
			.and(comment)
			.and(string("("))
			.and(comment)
			.and(formula, Struct3::setX)
			.and(comment)
			.and(string(")"))
			.and(comment)
			.and(line, Struct3::setY)
			.and(comment)
			.and(WithColor.withColor(named(regex("else\\b"), "else"), t -> Color.gray))
			.and(comment)
			.and(line, Struct3::setZ),
			t -> new LineIf(t, t.value.getX(), t.value.getY(), t.value.getZ())))
		.or(packNode(serial(Struct3<IWSFormula, IWSLine, IWSLine>::new)
			.and(WithColor.withColor(named(regex("if\\b"), "if"), t -> Color.gray))
			.and(comment)
			.and(string("("))
			.and(comment)
			.and(formula, Struct3::setX)
			.and(comment)
			.and(string(")"))
			.and(comment)
			.and(line, Struct3::setY),
			t -> new LineIf(t, t.value.getX(), t.value.getY())));
	public Syntax<IWSLine> lineStatic = packNode(extract((IWSLine) null)
		.and(WithColor.withColor(named(regex("static\\b"), "static"), t -> Color.gray))
		.and(comment)
		.extract(line),
		t -> new LineStatic(t, t.value));
	public Syntax<IWSLine> lineDefineVariableAssignment = packNode(serial(Struct3<String, String, IWSFormula>::new)
		.and(WithColor.withColor(identifier, t -> Color.gray), Struct3::setX)
		.and(comment)
		.and(identifier, Struct3::setY)
		.and(comment)
		.and(string("="))
		.and(comment)
		.and(formula, Struct3::setZ),
		n -> new LineDefineVariableAssignment(n, n.value.x, n.value.y, n.value.z));
	public Syntax<IWSLine> lineDefineVariable = packNode(serial(Struct2<String, String>::new)
		.and(WithColor.withColor(identifier, t -> Color.gray), Struct2::setX)
		.and(comment)
		.and(identifier, Struct2::setY),
		n -> new LineDefineVariable(n, n.value.x, n.value.y));
	public Syntax<IWSLine> lineAssignment = packNode(serial(Struct2<String, IWSFormula>::new)
		.and(identifier, Struct2::setX)
		.and(comment)
		.and(string("="))
		.and(comment)
		.and(formula, Struct2::setY),
		n -> new LineAssignment(n, n.value.x, n.value.y));
	public Syntax<IWSLine> lineVoid = packNode(string(";"),
		n -> new LineVoid(n));
	public Syntax<IWSLine> lineBrackets = extract((IWSLine) null)
		.and(string("{"))
		.extract(packNode(or((ArrayList<IWSLine>) null)
			.or(pack(serial(Struct2<IWSLine, ArrayList<IWSLine>>::new)
				.and(comment)
				.and(line, Struct2::setX)
				.and(repeat(extract((IWSLine) null)
					.and(comment)
					.extract(line)), Struct2::setY),
				t -> Stream.concat(
					Stream.of(t.getX()),
					t.getY().stream())
					.collect(Collectors.toCollection(ArrayList::new))))
			.or(pack(string(""),
				t -> new ArrayList<>())),
			n -> new LineBrackets(n, n.value)))
		.and(comment)
		.and(string("}"));

	{
		line.syntax = or((IWSLine) null)
			.or(lineIf)
			.or(lineStatic)
			.or(extract((IWSLine) null)
				.extract(or((IWSLine) null)
					.or(lineDefineVariableAssignment)
					.or(lineDefineVariable)
					.or(lineAssignment))
				.and(comment)
				.and(string(";")))
			.or(lineVoid)
			.or(lineBrackets);
	}

	public Syntax<IWSFormula> linesFormula = packNode(serial(Struct2<ArrayList<IWSLine>, IWSFormula>::new)
		.and(repeat(extract((IWSLine) null)
			.extract(line)
			.and(comment)), Struct2::setX)
		.and(formula, Struct2::setY),
		n -> new ExpressionRoot(n, n.value.x, n.value.y));

	// root

	public Syntax<IWSFormula> expression = linesFormula;

	public Syntax<IWSFormula> root = extract((IWSFormula) null)
		.and(comment)
		.extract(expression)
		.and(comment)
		.and(named(string(""), "EOF"));

	protected Syntax<IWSFormula> createLeft(
		Syntax<IWSFormula> term,
		Consumer<Consumer<Syntax<UnaryOperator<IWSFormula>>>> tokenMap)
	{
		SyntaxOr<UnaryOperator<IWSFormula>> tokens = or((UnaryOperator<IWSFormula>) null);
		tokenMap.accept((fomula) -> tokens.or(fomula));

		return pack(serial(
			Struct2<ArrayList<UnaryOperator<IWSFormula>>, IWSFormula>::new)
				.and(repeat(extract((UnaryOperator<IWSFormula>) null)
					.extract(tokens)
					.and(comment)), Struct2::setX)
				.and(term, Struct2::setY),
			t -> {
				IWSFormula right = t.y;
				for (UnaryOperator<IWSFormula> left : t.x) {
					right = left.apply(right);
				}
				return right;
			});
	}

	protected Syntax<IWSFormula> createOperatorLeft(
		Syntax<IWSFormula> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<IWSFormula, ArrayList<Struct2<String, IWSFormula>>>::new)
				.and(term, Struct2::setX)
				.and(repeat(serial(Struct2<String, IWSFormula>::new)
					.and(comment)
					.and(tokens, Struct2::setX)
					.and(comment)
					.and(term, Struct2::setY)), Struct2::setY),
			t -> {
				IWSFormula left = t.x;
				for (Struct2<String, IWSFormula> right : t.y) {
					left = new OperationFunction(left.getBegin(), right.y.getEnd(), right.x, left, right.y);
				}
				return left;
			});
	}

	protected Syntax<IWSFormula> createOperatorRight(
		Syntax<IWSFormula> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<ArrayList<Struct2<IWSFormula, String>>, IWSFormula>::new)
				.and(repeat(serial(Struct2<IWSFormula, String>::new)
					.and(term, Struct2::setX)
					.and(comment)
					.and(tokens, Struct2::setY)
					.and(comment)), Struct2::setX)
				.and(term, Struct2::setY),
			t -> {
				IWSFormula right = t.y;
				for (int i = t.x.size() - 1; i >= 0; i--) {
					right = new OperationFunction(t.x.get(i).x.getBegin(), right.getEnd(), t.x.get(i).y, t.x.get(i).x, right);
				}
				return right;
			});
	}

	protected Syntax<IWSFormula> createOperatorCompare(
		Syntax<IWSFormula> term,
		Consumer<BiConsumer<String, String>> tokenMap)
	{
		SyntaxOr<String> tokens = or((String) null);
		tokenMap.accept((token, name) -> tokens.or(pack(string(token), t -> name)));

		return pack(serial(
			Struct2<IWSFormula, ArrayList<Struct2<String, IWSFormula>>>::new)
				.and(term, Struct2::setX)
				.and(repeat(serial(Struct2<String, IWSFormula>::new)
					.and(comment)
					.and(tokens, Struct2::setX)
					.and(comment)
					.and(term, Struct2::setY)), Struct2::setY),
			t -> {
				if (t.y.size() == 0) return t.x;

				ArrayList<IWSFormula> formulas = new ArrayList<>();
				{
					IWSFormula left = t.x;
					for (Struct2<String, IWSFormula> right : t.y) {
						formulas.add(new OperationFunction(left.getBegin(), right.y.getEnd(), right.x, left, right.y));
						left = right.y;
					}
				}
				IWSFormula left = formulas.get(0);
				for (int i = 1; i < formulas.size(); i++) {
					IWSFormula right = formulas.get(i);
					left = new OperationFunction(left.getBegin(), right.getEnd(), "_operatorAmpersandAmpersand", left, right);
				}
				return left;
			});
	}

	protected <T> Syntax<ArrayList<T>> createArray(Syntax<T> formula, Syntax<?> separator)
	{
		return or((ArrayList<T>) null)
			.or(pack(serial(Struct2<T, ArrayList<T>>::new)
				.and(comment)
				.and(formula, Struct2::setX)
				.and(repeat(extract((T) null)
					.and(comment)
					.and(separator)
					.and(comment)
					.extract(formula)), Struct2::setY),
				t -> Stream.concat(Stream.of(t.x), t.y.stream())
					.collect(Collectors.toCollection(ArrayList::new))))
			.or(pack(extract((T) null)
				.and(comment)
				.extract(formula),
				t -> new ArrayList<>(Arrays.asList(t))))
			.or(pack(string(""),
				t -> new ArrayList<>()));
	}

}
