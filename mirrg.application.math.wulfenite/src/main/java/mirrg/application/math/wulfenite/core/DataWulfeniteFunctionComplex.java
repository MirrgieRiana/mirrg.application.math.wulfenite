package mirrg.application.math.wulfenite.core;

import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Entity;

public class DataWulfeniteFunctionComplex extends DataWulfeniteFunctionBase
{

	@Override
	protected Entity<Wulfenite> createEntity(Wulfenite game)
	{
		return new EntityWulfeniteFunctionComplex(game);
	}

	public class EntityWulfeniteFunctionComplex extends EntityWulfeniteFunctionBase
	{

		public EntityWulfeniteFunctionComplex(Wulfenite wulfenite)
		{
			super(wulfenite);
		}

		@Override
		public Object getValue(StructureComplex coordinate)
		{
			coordinate.re = 1 * coordinate.re;
			coordinate.im = 1 * coordinate.im;
			return coordinate;
		}

	}

}
