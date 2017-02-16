package mirrg.application.math.wulfenite.core;

import mirrg.helium.game.carbon.base.ModelCarbon;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.phosphorus.canvas.game.entity.ModelEntity.Entity;

public abstract class ModelMapperBase extends ModelCarbon<Wulfenite>
{

	public abstract class MapperBase extends Entity<Wulfenite> implements IMapper
	{

		public MapperBase(Wulfenite game)
		{
			super(game);
		}

		@Override
		public int getColor(StructureComplex coordinate)
		{
			return game.getModel().colorMapper.getController().getColor(getValue(coordinate));
		}

		@Override
		public String[] getValueInformation(StructureComplex coordinate)
		{
			Object value = getValue(coordinate);

			if (value instanceof StructureComplex) {
				return new String[] {
					"Re: " + ((StructureComplex) value).re,
					"Im: " + ((StructureComplex) value).im,
				};
			} else {
				return new String[] {
					"Value: " + value,
				};
			}
		}

		@Override
		public void toggleDialog()
		{

		}

	}

}
