package mirrg.application.math.wulfenite.script;

import java.awt.Color;
import java.util.stream.Collectors;

import mirrg.application.math.wulfenite.core.DataWulfeniteFunctionBase;
import mirrg.application.math.wulfenite.core.Wulfenite;
import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.compile.oxygen.parser.core.ResultOxygen;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.standard.hydrogen.struct.Struct1;
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

		private class ResultValidate
		{

			@SuppressWarnings("unused")
			private ResultOxygen<IWSFormula> result;

			private Environment environment;
			private StructureComplex input;
			private boolean isValid;

			private IWSFormula formula;

		}

		private ResultValidate validate(ResultOxygen<IWSFormula> result)
		{
			ResultValidate resultValidate = new ResultValidate();
			resultValidate.result = result;

			if (result.isValid) {

				// VM初期化
				{
					resultValidate.environment = new Environment();

					Variable<StructureComplex> variable = resultValidate.environment.addVariable("_", Type.COMPLEX);
					resultValidate.input = new StructureComplex();
					variable.value = resultValidate.input;

					Loader.loadEnvironment(resultValidate.environment);

				}

				// 意味解析
				resultValidate.isValid = result.node.value.validate(resultValidate.environment);
				if (resultValidate.isValid) {
					resultValidate.formula = result.node.value;
				}

			}

			return resultValidate;
		}

		private ResultValidate resultValidate;
		private Environment environment;
		private StructureComplex input;
		private IWSFormula formula;

		public void setSCompiler(ResultValidate resultValidate)
		{
			game.fireChangeFunction(() -> {
				this.resultValidate = resultValidate;
				if (resultValidate.isValid) {
					environment = resultValidate.environment;
					input = resultValidate.input;
					formula = resultValidate.formula;
				} else {
					environment = null;
					input = null;
					formula = null;
				}
			});
		}

		@Override
		public Object getValue(StructureComplex coordinate)
		{
			if (resultValidate == null) {
				setSCompiler(validate(WulfeniteScript.getSyntax().matches(source)));
			}
			if (formula != null) {
				input.set(coordinate);
				return formula.getValue();
			}
			return null;
		}

		private DialogWulfeniteScript dialog;

		@SuppressWarnings("unchecked")
		@Override
		public void toggleDialog()
		{
			if (dialog == null) {
				Struct1<ResultValidate> sResultValidate = new Struct1<>();

				dialog = new DialogWulfeniteScript(game.frame, source);

				dialog.textPaneOxygen.addProposalString("a()");

				dialog.textPaneOxygen.event().register(EventTextPaneOxygen.ChangeSource.class, e -> {
					source = dialog.textPaneOxygen.getText();
				});
				dialog.textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Success.class, e -> {
					ResultValidate resultValidate = validate((ResultOxygen<IWSFormula>) e.result);

					if (e.timing == EventTextPaneOxygen.Syntax.TIMING_MAIN) {
						sResultValidate.x = resultValidate;

						if (!resultValidate.isValid) {
							dialog.textPaneOut.setText(resultValidate.environment.getErrors()
								.map(t -> "[" + DialogWulfeniteScript.toPosition(source, t.getY().getBegin()) + "] " + t.getX())
								.collect(Collectors.joining("\n")));
							dialog.textPaneOut.setBackground(Color.decode("#ffddbb"));
						}
						setSCompiler(resultValidate);

					}
				});
				dialog.textPaneOxygen.event().register(EventTextPaneOxygen.Highlight.Post.class, e -> {
					if (!sResultValidate.x.isValid) {
						sResultValidate.x.environment.getErrors()
							.forEach(t -> {
								dialog.textPaneOxygen.setUnderline(
									t.getY().getBegin(),
									t.getY().getEnd() - t.getY().getBegin());
							});
					}
				});
			}
			dialog.setVisible(!dialog.isVisible());

			if (dialog.isVisible()) dialog.textPaneOxygen.update();
		}

		@Override
		public void dispose()
		{
			if (dialog != null) dialog.dispose();
		}

	}

}
