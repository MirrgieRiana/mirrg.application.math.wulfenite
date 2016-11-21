package mirrg.application.math.wulfenite.script;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Optional;
import java.util.stream.Collectors;

import mirrg.application.math.wulfenite.core.DataWulfeniteFunctionBase;
import mirrg.application.math.wulfenite.core.Wulfenite;
import mirrg.application.math.wulfenite.core.types.Type;
import mirrg.application.math.wulfenite.script.core.Environment;
import mirrg.application.math.wulfenite.script.core.Loader;
import mirrg.application.math.wulfenite.script.core.Variable;
import mirrg.application.math.wulfenite.script.core.WulfeniteScript;
import mirrg.application.math.wulfenite.script.node.IWSFormula;
import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.compile.oxygen.parser.core.ResultOxygen;
import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.standard.hydrogen.struct.Struct1;
import mirrg.helium.swing.phosphorus.canvas.EventPhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Entity;
import mirrg.helium.swing.phosphorus.canvas.game.render.Layer;
import mirrg.helium.swing.phosphorus.canvas.game.render.PointCoordinate;
import mirrg.helium.swing.phosphorus.canvas.game.render.PointScreen;
import mirrg.helium.swing.phosphorus.canvas.game.render.RectangleCoordinate;

public class DataWulfeniteFunctionScript extends DataWulfeniteFunctionBase
{

	public String source = "";
	public StructureComplex point1 = new StructureComplex();
	public StructureComplex point2 = new StructureComplex();

	@Override
	protected Entity<Wulfenite> createEntity(Wulfenite game)
	{
		return new EntityWulfeniteFunctionScript(game);
	}

	public class EntityWulfeniteFunctionScript extends EntityWulfeniteFunctionBase
	{

		private boolean pressingLeft;
		private boolean pressingRight;

		public EntityWulfeniteFunctionScript(Wulfenite game)
		{
			super(game);

			registerEvent(EventPhosphorusCanvas.EventMouse.Pressed.class, e -> {
				PointCoordinate point = game.getView().convert(new PointScreen(e.event.getPoint()));
				if (e.event.getButton() == MouseEvent.BUTTON1) {
					pressingLeft = true;

					game.fireChangeFunction(() -> point1.set(point.x, point.y));
					dirty(game.layerOverlay);
				} else if (e.event.getButton() == MouseEvent.BUTTON3) {
					pressingRight = true;

					game.fireChangeFunction(() -> point2.set(point.x, point.y));
					dirty(game.layerOverlay);
				}
			});
			registerEvent(EventPhosphorusCanvas.EventMouse.Released.class, e -> {
				PointCoordinate point = game.getView().convert(new PointScreen(e.event.getPoint()));
				if (e.event.getButton() == MouseEvent.BUTTON1) {
					pressingLeft = false;

					game.fireChangeFunction(() -> point1.set(point.x, point.y));
					dirty(game.layerOverlay);
				} else if (e.event.getButton() == MouseEvent.BUTTON3) {
					pressingRight = false;

					game.fireChangeFunction(() -> point2.set(point.x, point.y));
					dirty(game.layerOverlay);
				}
			});
			registerEvent(EventPhosphorusCanvas.EventMouseMotion.Dragged.class, e -> {
				PointCoordinate point = game.getView().convert(new PointScreen(e.event.getPoint()));
				if (pressingLeft) {
					game.fireChangeFunction(() -> point1.set(point.x, point.y));
					dirty(game.layerOverlay);
				}
				if (pressingRight) {
					game.fireChangeFunction(() -> point2.set(point.x, point.y));
					dirty(game.layerOverlay);
				}
			});
		}

		@Override
		public Optional<RectangleCoordinate> getOpticalBounds(Layer layer)
		{
			if (layer == game.layerOverlay) return Optional.of(game.getView().getCoordinateRectangle());
			return super.getOpticalBounds(layer);
		}

		@Override
		public void render(Layer layer)
		{
			if (layer == game.layerOverlay) {
				Graphics2D g = layer.getImageLayer().getGraphics();

				{
					PointScreen point = game.getView().convert(new PointCoordinate(point1.re, point1.im));
					g.setColor(Color.red);
					g.draw(new Line2D.Double(point.x - 5, point.y - 5, point.x + 5, point.y + 5));
					g.draw(new Line2D.Double(point.x - 5, point.y + 5, point.x + 5, point.y - 5));
				}

				{
					PointScreen point = game.getView().convert(new PointCoordinate(point2.re, point2.im));
					g.setColor(Color.yellow);
					g.draw(new Line2D.Double(point.x - 5, point.y - 5, point.x + 5, point.y + 5));
					g.draw(new Line2D.Double(point.x - 5, point.y + 5, point.x + 5, point.y - 5));
				}

			}
			super.render(layer);
		}

		private class ResultValidate
		{

			@SuppressWarnings("unused")
			private ResultOxygen<IWSFormula> result;

			private Environment environment;
			private Variable<StructureComplex> variable1;
			private Variable<StructureComplex> variable2;
			private Variable<StructureComplex> variable3;
			private boolean isValid;

			private IWSFormula formula;

			public Object getValue(StructureComplex coordinate)
			{
				if (formula != null) {
					variable1.value = coordinate;
					variable2.value = point1;
					variable3.value = point2;
					return formula.getValue();
				}
				return null;
			}

		}

		private ResultValidate validate(ResultOxygen<IWSFormula> result)
		{
			ResultValidate resultValidate = new ResultValidate();
			resultValidate.result = result;

			if (result.isValid) {

				// VM初期化
				{
					resultValidate.environment = new Environment();

					resultValidate.variable1 = resultValidate.environment.addVariable("x", Type.COMPLEX);
					resultValidate.variable2 = resultValidate.environment.addVariable("a", Type.COMPLEX);
					resultValidate.variable3 = resultValidate.environment.addVariable("b", Type.COMPLEX);
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

		public void setSCompiler(ResultValidate resultValidate)
		{
			game.fireChangeFunction(() -> this.resultValidate = resultValidate);
		}

		@Override
		public Object getValue(StructureComplex coordinate)
		{
			if (resultValidate == null) {
				setSCompiler(validate(WulfeniteScript.getSyntax().matches(source)));
			}
			return resultValidate.getValue(coordinate);
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
