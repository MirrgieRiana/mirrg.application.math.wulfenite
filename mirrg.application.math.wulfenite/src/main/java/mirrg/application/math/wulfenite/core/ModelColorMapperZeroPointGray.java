package mirrg.application.math.wulfenite.core;

public class ModelColorMapperZeroPointGray extends ModelColorMapperZeroPoint
{

	@Override
	public EntityColorMapperZeroPointGray getController()
	{
		return (EntityColorMapperZeroPointGray) super.getController();
	}

	@Override
	protected EntityColorMapperZeroPointGray createController(Wulfenite game)
	{
		return new EntityColorMapperZeroPointGray(game);
	}

	public class EntityColorMapperZeroPointGray extends EntityColorMapperZeroPoint
	{

		public EntityColorMapperZeroPointGray(Wulfenite game)
		{
			super(game);
		}

		@Override
		protected double getComplexR(double abs, double arg)
		{
			return 1;
		}

		@Override
		protected double getComplexG(double abs, double arg)
		{
			return 1;
		}

		@Override
		protected double getComplexB(double abs, double arg)
		{
			return 1;
		}

	}

}
