package mirrg.application.math.wulfenite.core;

import javax.swing.JDialog;

import mirrg.helium.swing.phosphorus.canvas.game.entity.ModelEntity;

public abstract class ModelColorMapperBase extends ModelEntity<Wulfenite>
{

	@Override
	public EntityColorMapperBase getController()
	{
		return (EntityColorMapperBase) super.getController();
	}

	public abstract class EntityColorMapperBase extends Entity<Wulfenite>
	{

		public EntityColorMapperBase(Wulfenite game)
		{
			super(game);
		}

		public abstract int getColor(Object value);

		public abstract JDialog createDialog(FrameWulfenite frame);

	}

}
