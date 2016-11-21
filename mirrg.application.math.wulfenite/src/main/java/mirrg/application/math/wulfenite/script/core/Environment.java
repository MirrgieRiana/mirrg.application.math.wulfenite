package mirrg.application.math.wulfenite.script.core;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.function.IWSFunction;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.application.math.wulfenite.script.node.IWSNode;
import mirrg.helium.standard.hydrogen.struct.Tuple;
import mirrg.helium.standard.hydrogen.struct.Tuple3;

public class Environment
{

	private Frame frame = new Frame();

	public void doInFrame(Runnable runnable)
	{
		Frame frame2 = new Frame(frame);
		frame = frame2;

		runnable.run();

		frame = frame2.parent;
	}

	public <T> Variable<T> addVariable(String name, Type<T> type)
	{
		return frame.addVariable(name, type);
	}

	public Optional<Variable<?>> getVariable(String name)
	{
		return frame.getVariable(name);
	}

	public Stream<Tuple<String, Variable<?>>> getVariables()
	{
		ArrayList<Tuple<String, Variable<?>>> dest = new ArrayList<>();
		frame.getVariables(dest);
		return dest.stream();
	}

	//

	private ArrayList<Tuple<String, IWSFunction>> functions = new ArrayList<>();

	public void addFunction(String name, IWSFunction wsFunction)
	{
		functions.add(new Tuple<>(name, wsFunction));
	}

	public Stream<Tuple3<String, IWSFunction, ArrayList<IWSFormula>>> getFunctions(String name, IWSFormula... args)
	{
		ArrayList<Tuple3<Tuple<String, IWSFunction>, Integer, ArrayList<IWSFormula>>> functions2 = getFunctions2(functions.stream()
			// 名前が異なるものは除外
			.filter(f -> f.getX().equals(name)), args);

		if (functions2.size() == 0) return Stream.empty();

		// 最短距離の関数だけを抽出
		return functions2.stream()
			.filter(t -> t.getY() == functions2.get(0).getY())
			.map(t -> new Tuple3<>(t.getX().getX(), t.getX().getY(), t.getZ()));
	}

	public Stream<Tuple3<String, IWSFunction, Boolean>> getFunctionsToProposal(String name, IWSFormula... args)
	{
		ArrayList<Tuple3<Tuple<String, IWSFunction>, Integer, ArrayList<IWSFormula>>> functions2 = getFunctions2(functions.stream(), args);
		return Stream.concat(
			functions2.stream()
				.sorted((a, b) -> a.getX().getX().compareTo(b.getX().getX()))
				.map(t -> new Tuple3<>(t.getX().getX(), t.getX().getY(), true)),
			functions.stream()
				.filter(f -> functions2.stream()
					.allMatch(t -> f != t.getX()))
				.sorted((a, b) -> a.getX().compareTo(b.getX()))
				.map(t -> new Tuple3<>(t.getX(), t.getY(), false)));
	}

	/**
	 * 登録済み関数を距離順に並び替え
	 */
	private ArrayList<Tuple3<Tuple<String, IWSFunction>, Integer, ArrayList<IWSFormula>>> getFunctions2(
		Stream<Tuple<String, IWSFunction>> functions,
		IWSFormula... args)
	{
		return functions

			// 引数の数が異なるものは除外
			.filter(f -> f.getY().getArgumentsType().size() == args.length)

			// 各引数のキャスト時の距離と関数を取得
			.map(f -> {
				ArrayList<Type<?>> argumentsType = f.getY().getArgumentsType();
				int distance = 0;
				ArrayList<IWSFormula> argumentsNew = new ArrayList<>();
				for (int i = 0; i < argumentsType.size(); i++) {
					Tuple<Integer, IWSFormula> casted = TypeHelper.cast(args[i], argumentsType.get(i));
					if (casted == null) return null;
					distance += casted.getX();
					argumentsNew.add(casted.getY());
				}
				return new Tuple3<>(f, distance, argumentsNew);
			})

			// キャストできなかったものを除く
			.filter(t -> t != null)

			// 距離が短い順にソート
			.sorted((a, b) -> a.getY() - b.getY())

			.collect(Collectors.toCollection(ArrayList::new));
	}

	//

	private ArrayList<Tuple<String, IWSNode>> errors = new ArrayList<>();

	public void reportError(String message, IWSNode formula)
	{
		errors.add(new Tuple<String, IWSNode>(message, formula));
	}

	public Stream<Tuple<String, IWSNode>> getErrors()
	{
		return errors.stream();
	}

}
