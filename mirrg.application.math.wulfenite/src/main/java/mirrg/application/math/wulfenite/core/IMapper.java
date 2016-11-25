package mirrg.application.math.wulfenite.core;

import mirrg.helium.math.hydrogen.complex.StructureComplex;

interface IMapper
{

	/**
	 * @param coordinate
	 *            破壊してもよい
	 * @return RGB
	 */
	public int getColor(StructureComplex coordinate);

	/**
	 * @param coordinate
	 *            破壊してもよい
	 * @return nullable
	 */
	public Object getValue(StructureComplex coordinate);

	public void toggleDialog();

	public void dispose();

	public String[] getValueInformation(StructureComplex coordinate);

}
