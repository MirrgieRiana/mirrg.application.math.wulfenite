package mirrg.application.math.wulfenite.core;

import mirrg.helium.math.hydrogen.complex.StructureComplex;

public interface IWulfeniteFunction
{

	/**
	 * @param coordinate
	 *            破壊してもよい
	 */
	public int getColor(StructureComplex coordinate);

	public boolean isValuePresent();

	/**
	 * @param buffer
	 *            入出力兼用
	 */
	public void getValue(StructureComplex buffer);

	public void toggleDialog();

	public void dispose();

}
