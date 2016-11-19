package mirrg.application.math.wulfenite.script;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.application.math.wulfenite.core.SlotDouble;
import mirrg.application.math.wulfenite.core.SlotInteger;
import mirrg.application.math.wulfenite.script.nodes.OperationCast;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.standard.hydrogen.struct.Tuple;
import mirrg.helium.standard.hydrogen.struct.Tuple3;

public class Environment
{

	private Hashtable<String, Variable> variables = new Hashtable<>();

	public Variable addVariable(String name, Class<?> type)
	{
		Variable variable = new Variable(type);
		variables.put(name, variable);
		return variable;
	}

	public Optional<Variable> getVariable(String name)
	{
		return Optional.ofNullable(variables.get(name));
	}

	//

	private ArrayList<IWulfeniteScriptFunction> functions = new ArrayList<>();

	public void addFunction(IWulfeniteScriptFunction wulfeniteScriptFunction)
	{
		functions.add(wulfeniteScriptFunction);
	}

	public ArrayList<Tuple<IWulfeniteScriptFunction, ArrayList<IWulfeniteScript>>> getFunction(String name, IWulfeniteScript... args)
	{

		// 登録済み関数を距離順に並び替え
		ArrayList<Tuple3<IWulfeniteScriptFunction, Integer, ArrayList<IWulfeniteScript>>> functions2 = functions.stream()

			// 名前が異なるものは除外
			.filter(f -> f.getName().equals(name))

			// 引数の数が異なるものは除外
			.filter(f -> f.getArgumentsType().size() == args.length)

			// 各引数のキャスト時の距離と関数を取得
			.map(f -> {
				ArrayList<Class<?>> argumentsType = f.getArgumentsType();
				int distance = 0;
				ArrayList<IWulfeniteScript> argumentsNew = new ArrayList<>();
				for (int i = 0; i < argumentsType.size(); i++) {
					Tuple<Integer, IWulfeniteScript> casted = cast(args[i], argumentsType.get(i));
					if (casted == null) return null;
					distance += casted.getX();
					argumentsNew.add(casted.getY());
				}
				return new Tuple3<>(f, distance, argumentsNew);
			})

			// キャストできなかったものを除く
			.filter(t -> t != null)

			// 最も距離の短いもの
			.sorted((a, b) -> a.getY() - b.getY())

			.collect(Collectors.toCollection(ArrayList::new));

		if (functions2.size() == 0) return new ArrayList<>();

		// 最短距離の関数だけを抽出
		return functions2.stream()
			.filter(t -> t.getY() == functions2.get(0).getY())
			.map(t -> new Tuple<>(t.getX(), t.getZ()))
			.collect(Collectors.toCollection(ArrayList::new));
	}

	public static Tuple<Integer, IWulfeniteScript> cast(IWulfeniteScript from, Class<?> to)
	{
		if (from.getType() == SlotInteger.class) {
			if (to == SlotDouble.class) {
				return new Tuple<>(1, new OperationCast(from, to) {

					private SlotDouble slot = new SlotDouble();

					@Override
					public Object getValue()
					{
						slot.value = ((SlotInteger) from.getValue()).value;
						return slot;
					}

				});
			}
			if (to == StructureComplex.class) {
				return new Tuple<>(2, new OperationCast(from, to) {

					private StructureComplex slot = new StructureComplex();

					@Override
					public Object getValue()
					{
						slot.re = ((SlotInteger) from.getValue()).value;
						return slot;
					}

				});
			}
		}
		if (from.getType() == SlotDouble.class) {
			if (to == StructureComplex.class) {
				return new Tuple<>(1, new OperationCast(from, to) {

					private StructureComplex slot = new StructureComplex();

					@Override
					public Object getValue()
					{
						slot.re = ((SlotDouble) from.getValue()).value;
						return slot;
					}

				});
			}
		}
		if (from.getType() == to) {
			return new Tuple<>(0, from);
		}
		return null;
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
