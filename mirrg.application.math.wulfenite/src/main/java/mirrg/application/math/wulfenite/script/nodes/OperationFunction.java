package mirrg.application.math.wulfenite.script.nodes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JLabel;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.IWulfeniteFormula;
import mirrg.application.math.wulfenite.script.IWulfeniteScriptFunction;
import mirrg.application.math.wulfenite.script.ScriptNodeBase;
import mirrg.application.math.wulfenite.script.TypeHelper;
import mirrg.helium.compile.oxygen.editor.IProviderChildren;
import mirrg.helium.compile.oxygen.editor.Proposal;
import mirrg.helium.compile.oxygen.parser.core.Node;
import mirrg.helium.standard.hydrogen.struct.Tuple3;

public class OperationFunction extends ScriptNodeBase implements IProviderChildren
{

	private String name;
	private Optional<TokenIdentifier> oTokenIdentifier;
	private IWulfeniteFormula[] args;

	public OperationFunction(int begin, int end, String name, IWulfeniteFormula... args)
	{
		super(begin, end);
		this.name = name;
		this.oTokenIdentifier = Optional.empty();
		this.args = args;
		args2 = new Object[args.length];
	}

	public OperationFunction(int begin, int end, TokenIdentifier tokenIdentifier, IWulfeniteFormula... args)
	{
		super(begin, end);
		this.name = tokenIdentifier.string;
		this.oTokenIdentifier = Optional.of(tokenIdentifier);
		this.args = args;
		args2 = new Object[args.length];
	}

	private IWulfeniteScriptFunction function;
	private IWulfeniteFormula[] args3;
	private Function<Object[], Object> function2;

	@Override
	protected boolean validateImpl(Environment environment)
	{
		boolean flag = true;

		// validate args
		{
			for (IWulfeniteFormula wulfeniteScript : args) {
				if (!wulfeniteScript.validate(environment)) flag = false;
			}
		}
		if (!flag) return false;

		// validate function
		{

			if (oTokenIdentifier.isPresent()) {
				oTokenIdentifier.get().providerProposal = () -> environment.getFunctionsToProposal(name, args)
					.collect(Collectors.toCollection(ArrayList::new)).stream()
					.map(s -> new Proposal(s.getX()) {
						@Override
						public void decorateListCellRendererComponent(JLabel label)
						{
							label.setText(s.getX() + "(" + s.getY().getArgumentsType().stream()
								.map(t -> t.getName())
								.collect(Collectors.joining(", ")) + ") : " + s.getY().getType().getName());

							label.setForeground(TypeHelper.getTokenColor(s.getY().getType()));

							if (!s.getZ()) label.setBackground(Color.decode("#dddddd"));
						}
					});
			}

			ArrayList<Tuple3<String, IWulfeniteScriptFunction, ArrayList<IWulfeniteFormula>>> functions = environment.getFunctions(name, args)
				.collect(Collectors.toCollection(ArrayList::new));

			if (functions.size() == 1) {
				function = functions.get(0).getY();
				args3 = functions.get(0).getZ().toArray(new IWulfeniteFormula[0]);
				function2 = function.createValueProvider();
			} else if (functions.size() == 0) {
				environment.reportError("No such function: " + name + "(" + Stream.of(args)
					.map(IWulfeniteFormula::getType)
					.map(Type::getName)
					.collect(Collectors.joining(", ")) + ")", this);
				flag = false;
			} else {
				environment.reportError("Ambiguous function call: " + name + "(" + Stream.of(args)
					.map(IWulfeniteFormula::getType)
					.map(Type::getName)
					.collect(Collectors.joining(", ")) + ")", this);
				flag = true;
			}
		}
		if (!flag) return false;

		return true;
	}

	@Override
	public Type<?> getType()
	{
		return function.getType();
	}

	private Object[] args2;

	@Override
	public Object getValue()
	{
		for (int i = 0; i < args.length; i++) {
			args2[i] = args3[i].getValue();
		}
		return function2.apply(args2);
	}

	@Override
	public List<Node<?>> getChildren()
	{
		if (oTokenIdentifier.isPresent()) {
			return Stream.concat(
				Stream.of(oTokenIdentifier.get().node),
				Stream.of(args)
					.map(a -> new Node<Object>(null, null, a.getBegin(), a.getEnd(), a)))
				.collect(Collectors.toCollection(ArrayList::new));
		} else {
			return Stream.of(args)
				.map(a -> new Node<Object>(null, null, a.getBegin(), a.getEnd(), a))
				.collect(Collectors.toCollection(ArrayList::new));
		}
	}

}
