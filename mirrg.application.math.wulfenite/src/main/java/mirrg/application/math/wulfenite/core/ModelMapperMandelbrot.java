package mirrg.application.math.wulfenite.core;

import mirrg.application.math.wulfenite.core.types.SlotInteger;
import mirrg.helium.game.carbon.base.ControllerCarbon;
import mirrg.helium.math.hydrogen.complex.StructureComplex;

public class ModelMapperMandelbrot extends ModelMapperBase
{

	@Override
	protected ControllerCarbon<Wulfenite> createController(Wulfenite game)
	{
		return new MapperMandelbrot(game);
	}

	public class MapperMandelbrot extends MapperBase
	{

		public MapperMandelbrot(Wulfenite wulfenite)
		{
			super(wulfenite);
		}

		private SlotInteger slot = new SlotInteger();

		@Override
		public Object getValue(StructureComplex coordinate)
		{
			int t = 0;
			double x2 = 0;
			double y2 = 0;
			while (t < 360 * 4) {
				x2 += coordinate.re;
				y2 += coordinate.im;
				double a = x2 * x2;
				double b = y2 * y2;
				if (a + b > 4) break;
				double tmp = a - b;
				y2 = 2 * x2 * y2;
				x2 = tmp;
				t++;
			}

			slot.value = t;
			return slot;
		}

	}

}
