package mirrg.application.math.wulfenite.core;

import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Entity;

public class DataWulfeniteFunctionDefault extends DataWulfeniteFunctionBase
{

	@Override
	protected Entity<Wulfenite> createEntity(Wulfenite game)
	{
		return new EntityWulfeniteFunctionDefault(game);
	}

	public class EntityWulfeniteFunctionDefault extends EntityWulfeniteFunctionBase
	{

		public EntityWulfeniteFunctionDefault(Wulfenite game)
		{
			super(game);
		}

		@Override
		public Object getValue(StructureComplex coordinate)
		{
			return null;
		}

	}

}
