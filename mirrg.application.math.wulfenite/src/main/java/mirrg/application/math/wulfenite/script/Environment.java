package mirrg.application.math.wulfenite.script;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.helium.standard.hydrogen.struct.Tuple;
import mirrg.helium.standard.hydrogen.struct.Tuple3;

public class Environment
{

	private Hashtable<String, Variable<?>> variables = new Hashtable<>();

	public <T> Variable<T> addVariable(String name, Type<T> type)
	{
		Variable<T> variable = new Variable<>(type);
		variables.put(name, variable);
		return variable;
	}

	public Optional<Variable<?>> getVariable(String name)
	{
		return Optional.ofNullable(variables.get(name));
	}

	public Stream<Tuple<String, Variable<?>>> getVariables()
	{
		return variables.entrySet().stream()
			.map(s -> new Tuple<>(s.getKey(), s.getValue()));
	}

	//

	private ArrayList<Tuple<String, IWulfeniteScriptFunction>> functions = new ArrayList<>();

	public void addFunction(String name, IWulfeniteScriptFunction wulfeniteScriptFunction)
	{
		functions.add(new Tuple<>(name, wulfeniteScriptFunction));
	}

	public Stream<Tuple3<String, IWulfeniteScriptFunction, ArrayList<IWulfeniteScript>>> getFunctions(String name, IWulfeniteScript... args)
	{
		ArrayList<Tuple3<Tuple<String, IWulfeniteScriptFunction>, Integer, ArrayList<IWulfeniteScript>>> functions2 = getFunctions2(functions.stream()
			// 名前が異なるものは除外
			.filter(f -> f.getX().equals(name)), args);

		if (functions2.size() == 0) return Stream.empty();

		// 最短距離の関数だけを抽出
		return functions2.stream()
			.filter(t -> t.getY() == functions2.get(0).getY())
			.map(t -> new Tuple3<>(t.getX().getX(), t.getX().getY(), t.getZ()));
	}

	public Stream<Tuple3<String, IWulfeniteScriptFunction, Boolean>> getFunctionsToProposal(String name, IWulfeniteScript... args)
	{
		ArrayList<Tuple3<Tuple<String, IWulfeniteScriptFunction>, Integer, ArrayList<IWulfeniteScript>>> functions2 = getFunctions2(functions.stream(), args);
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
	private ArrayList<Tuple3<Tuple<String, IWulfeniteScriptFunction>, Integer, ArrayList<IWulfeniteScript>>> getFunctions2(
		Stream<Tuple<String, IWulfeniteScriptFunction>> functions,
		IWulfeniteScript... args)
	{
		return functions

			// 引数の数が異なるものは除外
			.filter(f -> f.getY().getArgumentsType().size() == args.length)

			// 各引数のキャスト時の距離と関数を取得
			.map(f -> {
				ArrayList<Type<?>> argumentsType = f.getY().getArgumentsType();
				int distance = 0;
				ArrayList<IWulfeniteScript> argumentsNew = new ArrayList<>();
				for (int i = 0; i < argumentsType.size(); i++) {
					Tuple<Integer, IWulfeniteScript> casted = TypeHelper.cast(args[i], argumentsType.get(i));
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

	private ArrayList<Tuple<String, IWulfeniteScript>> errors = new ArrayList<>();

	public void reportError(String message, IWulfeniteScript wulfeniteScript)
	{
		errors.add(new Tuple<String, IWulfeniteScript>(message, wulfeniteScript));
	}

	public Stream<Tuple<String, IWulfeniteScript>> getErrors()
	{
		return errors.stream();
	}

}
