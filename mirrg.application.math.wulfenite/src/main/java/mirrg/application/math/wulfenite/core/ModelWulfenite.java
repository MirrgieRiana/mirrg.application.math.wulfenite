package mirrg.application.math.wulfenite.core;

import java.util.function.Consumer;

import mirrg.helium.game.carbon.base.ModelCarbon;
import mirrg.helium.swing.phosphorus.canvas.game.ModelPhosphorus;
import mirrg.helium.swing.phosphorus.canvas.game.view.ModelViewSkewed;

public class ModelWulfenite extends ModelPhosphorus<Wulfenite, ModelViewSkewed>
{

	public ModelMapperBase mapper;

	public ModelWulfenite(ModelViewSkewed view, ModelMapperBase mapper)
	{
		super(view);
		this.mapper = mapper;
	}

	@Override
	public void getChildModels(Consumer<ModelCarbon<? super Wulfenite>> dest)
	{
		super.getChildModels(dest);
		dest.accept(mapper);
	}

}
