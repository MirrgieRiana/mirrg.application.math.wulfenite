package mirrg.application.math.wulfenite.core;

import java.util.function.Consumer;

import mirrg.application.math.wulfenite.script.ModelMapperScript;
import mirrg.helium.game.carbon.base.ModelCarbon;
import mirrg.helium.swing.phosphorus.canvas.game.ModelPhosphorus;
import mirrg.helium.swing.phosphorus.canvas.game.view.ModelViewXYZoomXY;

public class ModelWulfenite extends ModelPhosphorus<Wulfenite, ModelViewXYZoomXY>
{

	public ModelMapperBase mapper;
	public ModelGrid grid;

	public static ModelWulfenite create()
	{
		ModelViewXYZoomXY view = new ModelViewXYZoomXY();
		view.zoomX = 0.01;
		view.zoomY = -0.01;
		return new ModelWulfenite(view);
	}

	public ModelWulfenite(ModelViewXYZoomXY view)
	{
		super(view);

		ModelMapperScript mapper = new ModelMapperScript();
		mapper.source = "(z + 1 + i) * (z - 1 - i) / (z - 1 + i) / (z + 1 - i)";
		this.mapper = mapper;

		this.grid = new ModelGrid();
	}

	@Override
	public void getChildModels(Consumer<ModelCarbon<? super Wulfenite>> dest)
	{
		super.getChildModels(dest);
		dest.accept(mapper);
		dest.accept(grid);
	}

}
