package mirrg.application.math.wulfenite.core;

import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.swing.phosphorus.canvas.EventPhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.game.EventGamePhosphorus;
import mirrg.helium.swing.phosphorus.canvas.game.entity.ModelEntity.Entity;
import mirrg.helium.swing.phosphorus.canvas.game.render.PointCoordinate;

public class ToolWulfenitePainter extends Entity<Wulfenite>
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
		registerGameEvent(EventGamePhosphorus.ChangeViewStatus.Post.class, e -> {
			dirty();
			updateCoordinate();
		});
		registerGameEvent(EventWulfenite.ChangeFunction.Post.class, e -> {
			dirty();
			updateCoordinate();
		});

	}

	@Override
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
			StructureComplex buffer = new StructureComplex();

			while (true) {
				IMapper function = game.getFunction();
				buffer.set(getCoordinateX(), getCoordinateY());
				int color;
				try {
					color = 0xff000000 | function.getColor(buffer);
				} catch (RuntimeException e) {
					e.printStackTrace();

					finishPainting();
					break;
				}
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
					finishPainting();
					break;
				}

				if (System.currentTimeMillis() > start + limitMs) break;

			}

		}
	}

	private void finishPainting()
	{
		x = 0;
		y = 0;
		updatedBeginX = 0;
		updatedBeginY = 0;
		isPainting = false;
	}

	//

	private double x0;
	private double y0;
	private double dx;
	private double dy;

	/**
	 * ペイントの終了座標を現在の座標に設定する。
	 */
	private synchronized void updateCoordinate()
	{
		PointCoordinate p = game.getView().getController().getCoordinateTopLeft();
		x0 = p.x;
		y0 = p.y;

		dx = game.getView().getController().getZoomX();
		dy = game.getView().getController().getZoomY();
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
