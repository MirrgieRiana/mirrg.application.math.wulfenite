package mirrg.application.math.wulfenite.core;

import javax.swing.JDialog;

import mirrg.application.math.wulfenite.core.types.SlotBoolean;
import mirrg.application.math.wulfenite.core.types.SlotColor;
import mirrg.application.math.wulfenite.core.types.SlotDouble;
import mirrg.application.math.wulfenite.core.types.SlotInteger;
import mirrg.application.math.wulfenite.core.types.SlotString;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.nitrogen.util.HColor;

public class ModelColorMapperDefault extends ModelColorMapperBase
{

	@Override
	public EntityColorMapperDefault getController()
	{
		return (EntityColorMapperDefault) super.getController();
	}

	@Override
	protected EntityColorMapperDefault createController(Wulfenite game)
	{
		return new EntityColorMapperDefault(game);
	}

	public class EntityColorMapperDefault extends EntityColorMapperBase
	{

		public EntityColorMapperDefault(Wulfenite game)
		{
			super(game);
		}

		@Override
		public int getColor(Object value)
		{
			if (value instanceof StructureComplex) {
				return getColorIntFromComplex((StructureComplex) value);
			} else if (value instanceof SlotColor) {
				return ((SlotColor) value).value;
			} else if (value instanceof SlotInteger) {
				return HColor.createColor(getBrightness(((SlotInteger) value).value) * 255, 0, 0).getRGB();
			} else if (value instanceof SlotDouble) {
				return HColor.createColor(getBrightness(((SlotDouble) value).value) * 255, 0, 0).getRGB();
			} else if (value instanceof SlotBoolean) {
				return ((SlotBoolean) value).value ? 0xffffff : 0x404040;
			} else if (value instanceof SlotString) {
				return ((SlotString) value).value.hashCode();
			} else if (value == null) {
				return 0x000000;
			} else {
				return value.hashCode();
			}
		}

		protected double getBrightness(double value)
		{
			value = value - Math.floor(value / 10) * 10;
			if (value > 5) value = 10 - value;
			value *= 0.2;
			return value;
		}

		protected int getColorIntFromComplex(StructureComplex coordinate)
		{
			return getColorIntFromPolar(coordinate.getAbstract(), coordinate.getArgument());
		}

		protected int getColorIntFromPolar(double abs, double arg)
		{
			double R = getComplexR(abs, arg);
			double G = getComplexG(abs, arg);
			double B = getComplexB(abs, arg);

			double brightness = getComplexBrightness(abs, arg);

			return createColor(
				255 * R * brightness,
				255 * G * brightness,
				255 * B * brightness);
		}

		protected double getComplexBrightness(double abs, double arg)
		{
			double brightness = 0.5 - 0.5 * Math.cos(abs * 2 * Math.PI);
			brightness = 1 - brightness * brightness;
			return brightness;
		}

		protected double getComplexR(double abs, double arg)
		{
			return 0.5 + 0.5 * Math.cos(arg);
		}

		protected double getComplexG(double abs, double arg)
		{
			return 0.5 + 0.5 * Math.cos(arg - 120.0 / 180 * Math.PI);
		}

		protected double getComplexB(double abs, double arg)
		{
			return 0.5 + 0.5 * Math.cos(arg - 240.0 / 180 * Math.PI);
		}

		// TODO mirrg
		protected int createColor(double r, double g, double b)
		{
			int r2 = (int) r;
			int g2 = (int) g;
			int b2 = (int) b;
			if (r2 < 0) r2 = 0;
			if (g2 < 0) g2 = 0;
			if (b2 < 0) b2 = 0;
			if (r2 > 255) r2 = 255;
			if (g2 > 255) g2 = 255;
			if (b2 > 255) b2 = 255;
			return (r2 << 16) | (g2 << 8) | b2;
		}

		@Override
		public JDialog createDialog(FrameWulfenite frame)
		{
			return null;
		}

	}

}
