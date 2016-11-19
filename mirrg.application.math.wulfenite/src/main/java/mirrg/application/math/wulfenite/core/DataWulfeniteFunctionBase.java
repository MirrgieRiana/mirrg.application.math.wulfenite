package mirrg.application.math.wulfenite.core;

import java.awt.Color;

import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.nitrogen.util.HColor;
import mirrg.helium.swing.phosphorus.canvas.game.existence.DataEntity;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Entity;

public abstract class DataWulfeniteFunctionBase extends DataEntity<Wulfenite>
{

	public abstract class EntityWulfeniteFunctionBase extends Entity<Wulfenite> implements IEntityWulfeniteFunction
	{

		public EntityWulfeniteFunctionBase(Wulfenite game)
		{
			super(game);
		}

		@Override
		public int getColor(StructureComplex coordinate)
		{
			Object value = getValue(coordinate);

			if (value instanceof StructureComplex) {
				return getColorIntFromComplex((StructureComplex) value);
			} else if (value instanceof SlotColor) {
				return ((SlotColor) value).rgb;
			} else if (value instanceof SlotInteger) {
				return HColor.createColor(128 - 128 * Math.cos(((SlotInteger) value).value * 3.1415 / 90), 0, 0).getRGB();
			} else if (value instanceof SlotDouble) {
				return HColor.createColor(128 - 128 * Math.cos(((SlotDouble) value).value * 3.1415 / 90), 0, 0).getRGB();
			} else if (value instanceof Color) {
				return ((Color) value).getRGB();
			} else if (value instanceof Integer) {
				return HColor.createColor(128 - 128 * Math.cos(((Integer) value) * 3.1415 / 90), 0, 0).getRGB();
			} else if (value instanceof Double) {
				return HColor.createColor(128 - 128 * Math.cos(((Double) value) * 3.1415 / 90), 0, 0).getRGB();
			} else if (value == null) {
				return 0x000000;
			} else {
				return value.hashCode();
			}
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

		public int getColorIntFromComplex(StructureComplex coordinate)
		{
			return getColorIntFromPolar(coordinate.getAbstract(), coordinate.getArgument());
		}

		public int getColorIntFromPolar(double abs, double arg)
		{
			double R = Math.cos(arg);
			double G = Math.cos(arg - 120.0 / 180 * Math.PI);
			double B = Math.cos(arg - 240.0 / 180 * Math.PI);

			double brightness = 0.5 - 0.5 * Math.cos(abs * 2 * Math.PI);

			brightness = 1 - brightness * brightness;

			return HColor.getColorInt(
				(int) ((128 + (126 * R)) * brightness),
				(int) ((128 + (126 * G)) * brightness),
				(int) ((128 + (126 * B)) * brightness));
		}

		@Override
		public void toggleDialog()
		{

		}

	}

}
