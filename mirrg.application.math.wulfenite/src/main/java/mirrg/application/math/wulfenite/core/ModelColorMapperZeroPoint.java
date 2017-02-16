package mirrg.application.math.wulfenite.core;

import javax.swing.JDialog;

import mirrg.application.math.wulfenite.core.dialogs.DialogColorZeroPoint;

public class ModelColorMapperZeroPoint extends ModelColorMapperDefault
{

	public double color2_a = 1;
	public double color2_b = 0;
	public double color2_c = 1;

	@Override
	public EntityColorMapperZeroPoint getController()
	{
		return (EntityColorMapperZeroPoint) super.getController();
	}

	@Override
	protected EntityColorMapperZeroPoint createController(Wulfenite game)
	{
		return new EntityColorMapperZeroPoint(game);
	}

	public class EntityColorMapperZeroPoint extends EntityColorMapperDefault
	{

		public EntityColorMapperZeroPoint(Wulfenite game)
		{
			super(game);
		}

		@Override
		protected double getComplexBrightness(double abs, double arg)
		{
			return abs > color2_a ? color2_c : abs * (color2_c - color2_b) / color2_a + color2_b;
		}

		@Override
		public JDialog createDialog(FrameWulfenite frame)
		{
			return new DialogColorZeroPoint(game, frame, ModelColorMapperZeroPoint.this);
		}

	}

}
