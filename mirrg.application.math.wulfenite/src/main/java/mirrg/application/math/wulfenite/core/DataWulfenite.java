package mirrg.application.math.wulfenite.core;

import mirrg.helium.swing.phosphorus.canvas.game.Data;

public class DataWulfenite extends Data<Wulfenite>
{

	public DataWulfeniteFunctionBase wulfeniteFunction = new DataWulfeniteFunctionDefault();

	@Override
	public void initialize(Wulfenite game)
	{
		super.initialize(game);
		wulfeniteFunction.initialize(game);
	}

	@Override
	public void dispose()
	{
		super.dispose();
		wulfeniteFunction.dispose();
	}

}
