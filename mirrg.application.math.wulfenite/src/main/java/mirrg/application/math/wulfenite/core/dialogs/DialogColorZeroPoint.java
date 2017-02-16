package mirrg.application.math.wulfenite.core.dialogs;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import javax.swing.JDialog;
import javax.swing.JFrame;

import mirrg.application.math.wulfenite.core.ModelColorMapperZeroPoint;
import mirrg.application.math.wulfenite.core.Wulfenite;
import mirrg.application.math.wulfenite.core.dialog.ParameterDouble;
import mirrg.helium.swing.phosphorus.canvas.game.EventGamePhosphorus;

public class DialogColorZeroPoint extends JDialog
{

	private Wulfenite game;

	private ParameterDouble a;
	private ParameterDouble b;
	private ParameterDouble c;

	public DialogColorZeroPoint(Wulfenite game, JFrame frame, ModelColorMapperZeroPoint model)
	{
		super(frame, "色設定");
		this.game = game;

		a = new ParameterDouble("色変化閾値", () -> model.color2_a, a -> model.color2_a = a, this::pre, this::post);
		b = new ParameterDouble("零点輝度(0~1)", () -> model.color2_b, a -> model.color2_b = a, this::pre, this::post);
		c = new ParameterDouble("閾値輝度(0~1)", () -> model.color2_c, a -> model.color2_c = a, this::pre, this::post);

		add(createBorderPanelDown(
			createBorderPanelUp(
				a.initRecord(),
				b.initRecord(),
				c.initRecord(),
				null),
			createBorderPanelLeft(
				createButton("零点：黒", e -> {
					a.set((double) 1);
					b.set((double) 0);
					c.set((double) 1);
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
