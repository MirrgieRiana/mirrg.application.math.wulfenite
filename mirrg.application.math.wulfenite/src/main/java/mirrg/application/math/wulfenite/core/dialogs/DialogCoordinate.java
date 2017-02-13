package mirrg.application.math.wulfenite.core.dialogs;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import javax.swing.JDialog;
import javax.swing.JFrame;

import mirrg.application.math.wulfenite.core.Wulfenite;
import mirrg.application.math.wulfenite.core.dialog.ParameterDouble;
import mirrg.helium.swing.phosphorus.canvas.game.EventGamePhosphorus;

public class DialogCoordinate extends JDialog
{

	private Wulfenite game;

	private ParameterDouble x;
	private ParameterDouble y;
	private ParameterDouble zoomX;
	private ParameterDouble zoomY;

	public DialogCoordinate(Wulfenite game, JFrame frame)
	{
		super(frame, "座標設定");
		this.game = game;

		x = new ParameterDouble("X", () -> game.getView().x, a -> game.getView().x = a, this::pre, this::post);
		y = new ParameterDouble("Y", () -> game.getView().y, a -> game.getView().y = a, this::pre, this::post);
		zoomX = new ParameterDouble("Zoom X", () -> game.getView().zoomX, a -> game.getView().zoomX = a, this::pre, this::post);
		zoomY = new ParameterDouble("Zoom Y", () -> game.getView().zoomY, a -> game.getView().zoomY = a, this::pre, this::post);

		add(createBorderPanelDown(
			createBorderPanelUp(
				x.initRecord(),
				y.initRecord(),
				zoomX.initRecord(),
				zoomY.initRecord(),
				null),
			createBorderPanelLeft(
				createButton("リセット", e -> {
					x.set((double) 1);
					y.set((double) 1);
					zoomX.set((double) 100);
					zoomY.set((double) 100);
					post();
				}),
				null)));
	}

	private void pre()
	{
		game.event().post(new EventGamePhosphorus.ChangeViewStatus.Pre());
	}

	private void post()
	{
		game.event().post(new EventGamePhosphorus.ChangeViewStatus.Post());
	}

}
