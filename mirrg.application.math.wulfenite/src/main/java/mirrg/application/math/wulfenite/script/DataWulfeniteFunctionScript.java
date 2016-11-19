package mirrg.application.math.wulfenite.script;

import java.awt.Color;
import java.util.Optional;
import java.util.stream.Collectors;

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

		private Environment environment;
		private StructureComplex input;

		private void onChangeSource()
		{
			ResultOxygen<IWulfeniteScript> result = WulfeniteScript.getSyntax().matches(source);
			if (result.isValid) {

				// VM初期化
				{
					environment = new Environment();

					Variable variable = environment.addVariable("_", StructureComplex.class);
					input = new StructureComplex();
					variable.value = input;

					Loader.loadFunction(environment);

				}

				// 意味解析
				if (result.node.value.validate(environment)) {
					game.fireChangeFunction(() -> {
						consumer = Optional.of(result.node.value);
					});
				} else {
					consumer = Optional.empty();
					dialog.textPaneOut.setText(environment.getErrors()
						.map(t -> "[" + DialogWulfeniteScript.toPosition(source, t.getY().getBegin()) + "] " + t.getX())
						.collect(Collectors.joining("\n")));
					dialog.textPaneOut.setBackground(Color.decode("#ffddbb"));
				}

			} else {
				environment = null;
				consumer = Optional.empty();
			}
		}

		@Override
		public Object getValue(StructureComplex coordinate)
		{
			if (consumer == null) onChangeSource();
			if (consumer.isPresent()) {
				input.set(coordinate);
				return consumer.get().getValue();
			}
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
				dialog.textPaneOxygen.event().register(EventTextPaneOxygen.Highlight.Post.class, e -> {
					if (environment != null) {
						environment.getErrors()
							.forEach(t -> {
								dialog.textPaneOxygen.setUnderline(
									t.getY().getBegin(),
									t.getY().getEnd() - t.getY().getBegin());
							});
					}
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
