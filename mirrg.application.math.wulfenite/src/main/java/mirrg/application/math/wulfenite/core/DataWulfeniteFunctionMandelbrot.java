package mirrg.application.math.wulfenite.core;

import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Entity;

public class DataWulfeniteFunctionMandelbrot extends DataWulfeniteFunctionBase
{

	@Override
	protected Entity<Wulfenite> createEntity(Wulfenite game)
	{
		return new EntityWulfeniteFunctionMandelbrot(game);
	}

	public class EntityWulfeniteFunctionMandelbrot extends EntityWulfeniteFunctionBase
	{

		public EntityWulfeniteFunctionMandelbrot(Wulfenite wulfenite)
		{
			super(wulfenite);
		}

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

			return t;
		}

	}

}
