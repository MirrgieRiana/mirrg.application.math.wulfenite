package mirrg.application.math.wulfenite.core;

import javax.swing.JDialog;

import mirrg.application.math.wulfenite.core.dialogs.DialogColor2;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.nitrogen.util.HColor;

public class ModelColorMapper2 extends ModelColorMapperDefault
{

	public double color2_a = 1;
	public double color2_b = 0;
	public double color2_c = 1;

	@Override
	public EntityColorMapper2 getController()
	{
		return (EntityColorMapper2) super.getController();
	}

	@Override
	protected EntityColorMapper2 createController(Wulfenite game)
	{
		return new EntityColorMapper2(game);
	}

	public class EntityColorMapper2 extends EntityColorMapperDefault
	{

		public EntityColorMapper2(Wulfenite game)
		{
			super(game);
		}

		@Override
		protected int getColorIntFromComplex(StructureComplex coordinate)
		{
			return getColorIntFromPolar(coordinate.getAbstract(), coordinate.getArgument());
		}

		@Override
		protected int getColorIntFromPolar(double abs, double arg)
		{
			double R = Math.cos(arg);
			double G = Math.cos(arg - 120.0 / 180 * Math.PI);
			double B = Math.cos(arg - 240.0 / 180 * Math.PI);

			double brightness = abs > color2_a ? color2_c : abs * (color2_c - color2_b) / color2_a + color2_b;

			return HColor.getColorInt(
				(int) ((128 + (126 * R)) * brightness),
				(int) ((128 + (126 * G)) * brightness),
				(int) ((128 + (126 * B)) * brightness));
		}

		@Override
		public JDialog createDialog(FrameWulfenite frame)
		{
			return new DialogColor2(game, frame, ModelColorMapper2.this);
		}

	}

}
