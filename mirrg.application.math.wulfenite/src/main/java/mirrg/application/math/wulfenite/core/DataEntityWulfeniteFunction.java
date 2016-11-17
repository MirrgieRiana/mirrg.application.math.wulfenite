package mirrg.application.math.wulfenite.core;

import mirrg.helium.swing.phosphorus.canvas.game.existence.DataEntity;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Entity;

public class DataEntityWulfeniteFunction extends DataEntity<Wulfenite>
{

	public IWulfeniteFunction wulfeniteFunction = new WulfeniteFunctionDefault();

	@Override
	protected Entity<Wulfenite> createEntity(Wulfenite game)
	{
		return new EntityWulfeniteFunction(game);
	}

	public class EntityWulfeniteFunction extends Entity<Wulfenite>
	{

		public EntityWulfeniteFunction(Wulfenite game)
		{
			super(game);
		}

	}

}
