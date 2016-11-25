package mirrg.application.math.wulfenite.core;

import mirrg.helium.game.carbon.base.ControllerCarbon;
import mirrg.helium.math.hydrogen.complex.StructureComplex;

public class ModelMapperDefault extends ModelMapperBase
{

	@Override
	protected ControllerCarbon<Wulfenite> createController(Wulfenite game)
	{
		return new MapperDefault(game);
	}

	public class MapperDefault extends MapperBase
	{

		public MapperDefault(Wulfenite game)
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
