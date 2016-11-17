package mirrg.application.math.wulfenite.core;

import mirrg.helium.swing.phosphorus.canvas.EventPhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.game.EventPhosphorusGame;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Tool;
import mirrg.helium.swing.phosphorus.canvas.game.render.PointCoordinate;

public class ToolWulfenitePainter extends Tool<Wulfenite>
{

	public int limitMs;

	private int x = 0;
	private int y = 0;
	private int updatedBeginX = 0;
	private int updatedBeginY = 0;
	private boolean isPainting = true;

	public ToolWulfenitePainter(Wulfenite wulfenite, int limitMs)
	{
		super(wulfenite);
		this.limitMs = limitMs;

		updateCoordinate();

		registerEvent(EventPhosphorusCanvas.EventComponent.Resized.class, e -> {
			dirty();
			updateCoordinate();
		});
		registerGameEvent(EventPhosphorusGame.ViewChange.Post.class, e -> {
			dirty();
			updateCoordinate();
		});
		registerGameEvent(EventWulfenite.ChangeFunction.Post.class, e -> {
			dirty();
		});

	}

	public synchronized void dirty()
	{
		updatedBeginX = x;
		updatedBeginY = y;
		isPainting = true;
	}

	@Override
	public synchronized void move()
	{
		if (isPainting) {
			long start = System.currentTimeMillis();

			while (true) {

				int color = game.getFunction().getColor(getCoordinateX(), getCoordinateY());
				try {
					game.layerMath.getImageLayer().getImage().setRGB(x, y, color);
				} catch (ArrayIndexOutOfBoundsException e) {

				}

				x++;
				if (x > game.canvas.getWidth()) {
					x = 0;

					y++;
					if (y > game.canvas.getHeight()) {
						y = 0;
					}
				}

				// 全て塗り替えた
				if (x == updatedBeginX && y == updatedBeginY) {
					x = 0;
					y = 0;
					updatedBeginX = 0;
					updatedBeginY = 0;
					isPainting = false;

					break;
				}

				if (System.currentTimeMillis() > start + limitMs) break;

			}

		}
	}

	//

	private double x0;
	private double y0;
	private double dx;
	private double dy;

	private synchronized void updateCoordinate()
	{
		PointCoordinate p = game.getView().getCoordinateTopLeft();
		x0 = p.x;
		y0 = p.y;

		dx = game.getView().getZoomX();
		dy = game.getView().getZoomY();
	}

	public double getCoordinateX()
	{
		return x0 + dx * x;
	}

	public double getCoordinateY()
	{
		return y0 + dy * y;
	}

}
