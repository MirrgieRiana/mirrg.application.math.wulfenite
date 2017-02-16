package mirrg.application.math.wulfenite.core;

public class ModelColorMapperZeroPointGlay extends ModelColorMapperZeroPoint
{

	@Override
	public EntityColorMapperZeroPointGlay getController()
	{
		return (EntityColorMapperZeroPointGlay) super.getController();
	}

	@Override
	protected EntityColorMapperZeroPointGlay createController(Wulfenite game)
	{
		return new EntityColorMapperZeroPointGlay(game);
	}

	public class EntityColorMapperZeroPointGlay extends EntityColorMapperZeroPoint
	{

		public EntityColorMapperZeroPointGlay(Wulfenite game)
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
