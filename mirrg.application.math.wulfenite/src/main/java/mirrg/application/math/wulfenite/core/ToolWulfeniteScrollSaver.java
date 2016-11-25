package mirrg.application.math.wulfenite.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mirrg.helium.swing.phosphorus.canvas.EventImageLayer;
import mirrg.helium.swing.phosphorus.canvas.game.EventGamePhosphorus;
import mirrg.helium.swing.phosphorus.canvas.game.entity.ModelEntity.Entity;
import mirrg.helium.swing.phosphorus.canvas.game.render.RectangleCoordinate;
import mirrg.helium.swing.phosphorus.canvas.game.render.RectangleScreen;

public class ToolWulfeniteScrollSaver extends Entity<Wulfenite>
{

	private RectangleCoordinate rectangle;
	private BufferedImage image;

	public ToolWulfeniteScrollSaver(Wulfenite game)
	{
		super(game);

		rebuffer();
		registerEvent(game.layerMath.getImageLayer().event(), EventImageLayer.Rebuffer.Post.class, e -> {
			rebuffer();
		});

		registerGameEvent(EventGamePhosphorus.ChangeViewStatus.Pre.class, e -> {
			rectangle = game.getView().getController().getCoordinateRectangle();

			image.createGraphics().drawImage(game.layerMath.getImageLayer().getImage(), 0, 0, null);
		});
		registerGameEvent(EventGamePhosphorus.ChangeViewStatus.Post.class, e -> {
			if (rectangle == null) return;
			Graphics2D g = game.layerMath.getImageLayer().getGraphics();

			RectangleScreen rectangle2 = game.getView().getController().convert(rectangle);

			g.drawImage(
				image,
				(int) Math.round(rectangle2.x),
				(int) Math.round(rectangle2.y),
				(int) Math.round(rectangle2.x + rectangle2.width),
				(int) Math.round(rectangle2.y + rectangle2.height),
				0,
				0,
				game.canvas.getWidth(),
				game.canvas.getHeight(),
				null);
		});
	}

	private void rebuffer()
	{
		image = new BufferedImage(
			game.layerMath.getImageLayer().getImage().getWidth(),
			game.layerMath.getImageLayer().getImage().getHeight(),
			game.layerMath.getImageLayer().getImage().getType());
	}

}
