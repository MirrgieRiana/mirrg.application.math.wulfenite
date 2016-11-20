package mirrg.application.math.wulfenite.script.nodes;

import java.util.stream.Stream;

import mirrg.helium.compile.oxygen.editor.IProviderProposal;
import mirrg.helium.compile.oxygen.editor.Proposal;
import mirrg.helium.compile.oxygen.parser.core.IListenerNode;
import mirrg.helium.compile.oxygen.parser.core.Node;

public class TokenIdentifier implements IProviderProposal, IListenerNode
{

	public String string;

	public TokenIdentifier(String identifier)
	{
		this.string = identifier;
	}

	//

	public Node<?> node;

	@Override
	public void setNode(Node<?> node)
	{
		this.node = node;
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
