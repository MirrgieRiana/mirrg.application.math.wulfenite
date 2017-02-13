package mirrg.application.math.wulfenite.core.dialogs;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import javax.swing.JDialog;
import javax.swing.JFrame;

import mirrg.application.math.wulfenite.core.Wulfenite;
import mirrg.application.math.wulfenite.core.dialog.ParameterDouble;
import mirrg.application.math.wulfenite.core.dialog.ParameterString;
import mirrg.helium.swing.phosphorus.canvas.game.render.Layer;

public class DialogUnit extends JDialog
{

	private Wulfenite game;

	private ParameterDouble x;
	private ParameterDouble y;
	private ParameterString nameX;
	private ParameterString nameY;

	public DialogUnit(Wulfenite game, JFrame frame)
	{
		super(frame, "単位設定");
		this.game = game;

		x = new ParameterDouble("実軸単位", () -> game.getModel().grid.unitX, a -> game.getModel().grid.unitX = a, this::pre, this::post);
		y = new ParameterDouble("虚軸単位", () -> game.getModel().grid.unitY, a -> game.getModel().grid.unitY = a, this::pre, this::post);
		nameX = new ParameterString("実軸単位名", () -> game.getModel().grid.unitNameY, a -> game.getModel().grid.unitNameY = a, this::pre, this::post);
		nameY = new ParameterString("虚軸単位名", () -> game.getModel().grid.unitNameY, a -> game.getModel().grid.unitNameY = a, this::pre, this::post);

		add(createBorderPanelDown(
			createBorderPanelUp(
				x.initRecord(),
				nameX.initRecord(),
				y.initRecord(),
				nameY.initRecord(),
				null),
			createBorderPanelLeft(
				createButton("リセット", e -> {
					x.set((double) 1);
					y.set((double) 1);
					nameX.set("");
					nameY.set("");
					post();
				}), createButton("π", e -> {
					x.set(Math.PI);
					y.set(Math.PI);
					nameX.set("π");
					nameY.set("π");
					post();
				}),
				null)));
	}

	private void pre()
	{

	}

	private void post()
	{
		game.getLayers().forEach(Layer::dirty);
	}

}
