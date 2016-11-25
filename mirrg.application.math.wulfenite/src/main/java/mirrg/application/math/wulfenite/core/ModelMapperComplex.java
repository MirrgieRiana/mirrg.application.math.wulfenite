package mirrg.application.math.wulfenite.core;

import mirrg.helium.game.carbon.base.ControllerCarbon;
import mirrg.helium.math.hydrogen.complex.StructureComplex;

public class ModelMapperComplex extends ModelMapperBase
{

	@Override
	protected ControllerCarbon<Wulfenite> createController(Wulfenite game)
	{
		return new MapperComplex(game);
	}

	public class MapperComplex extends MapperBase
	{

		public MapperComplex(Wulfenite wulfenite)
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
