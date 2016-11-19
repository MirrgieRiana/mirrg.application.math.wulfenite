package mirrg.application.math.wulfenite.script;

import java.util.Optional;

import mirrg.application.math.wulfenite.core.DataWulfeniteFunctionBase;
import mirrg.application.math.wulfenite.core.Wulfenite;
import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.compile.oxygen.parser.core.ResultOxygen;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Entity;

public class DataWulfeniteFunctionScript extends DataWulfeniteFunctionBase
{

	public String source = "";

	@Override
	protected Entity<Wulfenite> createEntity(Wulfenite game)
	{
		return new EntityWulfeniteFunctionScript(game);
	}

	public class EntityWulfeniteFunctionScript extends EntityWulfeniteFunctionBase
	{

		public EntityWulfeniteFunctionScript(Wulfenite game)
		{
			super(game);
		}

		private Optional<IWulfeniteScript> consumer;

		private void onChangeSource()
		{
			ResultOxygen<IWulfeniteScript> result = WulfeniteScript.getSyntax().matches(source);
			game.fireChangeFunction(() -> {
				consumer = result.isValid ? Optional.of(result.node.value) : Optional.empty();
			});
		}

		@Override
		public Object getValue(StructureComplex coordinate)
		{
			if (consumer == null) onChangeSource();
			if (consumer.isPresent()) return consumer.get().getValue();
			return null;
		}

		private DialogWulfeniteScript dialog;

		@Override
		public void toggleDialog()
		{
			if (dialog == null) {
				dialog = new DialogWulfeniteScript(game.frame, source);
				dialog.textPaneOxygen.event().register(EventTextPaneOxygen.ChangeSource.class, e -> {
					source = dialog.textPaneOxygen.getText();
				});
				dialog.textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Success.class, e -> {
					onChangeSource();
				});
			}
			dialog.setVisible(!dialog.isVisible());
		}

		@Override
		public void dispose()
		{
			if (dialog != null) dialog.dispose();
		}

	}

}
