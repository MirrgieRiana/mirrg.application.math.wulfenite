package mirrg.application.math.wulfenite.script.nodes;

import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.JLabel;

import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.Environment;
import mirrg.application.math.wulfenite.script.TypeHelper;
import mirrg.application.math.wulfenite.script.Variable;
import mirrg.application.math.wulfenite.script.node.WSFormulaBase;
import mirrg.helium.compile.oxygen.editor.IProviderProposal;
import mirrg.helium.compile.oxygen.editor.Proposal;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class OperationVariable extends WSFormulaBase implements IProviderProposal
{

	private String name;

	public OperationVariable(Node<?> node, String name)
	{
		super(node);
		this.name = name;
	}

	private Variable<?> variable;

	@Override
	protected boolean validateImpl(Environment environment)
	{
		boolean flag = true;

		{
			Optional<Variable<?>> oVariable = environment.getVariable(name);

			providerProposal = () -> environment.getVariables()
				.sorted((a, b) -> a.getX().compareTo(b.getX()))
				.map(t -> new Proposal(t.getX()) {
					@Override
					public void decorateListCellRendererComponent(JLabel label)
					{
						label.setText(t.getX() + " : " + t.getY().type.getName());
						label.setForeground(TypeHelper.getTokenColor(t.getY().type));
					}
				});

			if (oVariable.isPresent()) {
				variable = oVariable.get();
			} else {
				environment.reportError("No such variable: " + name, this);
				flag = false;
			}
		}
		if (!flag) return false;

		return true;
	}

	@Override
	public Type<?> getType()
	{
		return variable.type;
	}

	@Override
	public Object getValue()
	{
		return variable.value;
	}

	//

	public IProviderProposal providerProposal;
	private Stream<Proposal> cache;

	@Override
	public Stream<Proposal> getProposals()
	{
		if (cache == null) cache = providerProposal.getProposals();
		return cache;
	}

}
