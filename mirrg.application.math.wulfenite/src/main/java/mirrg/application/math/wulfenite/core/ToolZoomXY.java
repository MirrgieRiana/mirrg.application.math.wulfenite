package mirrg.application.math.wulfenite.core;

import mirrg.helium.swing.phosphorus.canvas.EventPhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.game.entity.ModelEntity.Entity;
import mirrg.helium.swing.phosphorus.canvas.game.render.PointScreen;
import mirrg.helium.swing.phosphorus.canvas.game.view.ModelViewXYZoomXY.ControllerViewXYZoomXY;

public class ToolZoomXY extends Entity<Wulfenite>
{

	public double deltaZoom = 1.1;
	private PointScreen point;

	public ToolZoomXY(Wulfenite game)
	{
		super(game);

		registerEvent(EventPhosphorusCanvas.EventWheel.Moved.class, e -> {
			doZoom(e.event.getWheelRotation(), e.event.isShiftDown(), e.event.isControlDown(), e.event.isAltDown());
		});
		registerEvent(EventPhosphorusCanvas.EventMouseMotion.Moved.class, e -> {
			point = new PointScreen(e.event.getPoint());
		});
		registerEvent(EventPhosphorusCanvas.EventMouseMotion.Dragged.class, e -> {
			point = new PointScreen(e.event.getPoint());
		});

	}

	public void doZoom(int scrollAmount)
	{
		doZoom(scrollAmount, true, true);
	}

	public void doZoom(int scrollAmount, boolean isShift, boolean isControl, boolean isAlt)
	{
		if (isShift) {
			doZoom(scrollAmount, false, true);
			return;
		}
		if (isControl) {
			doZoom(scrollAmount, true, false);
			return;
		}
		doZoom(scrollAmount, true, true);
	}

	public void doZoom(int scrollAmount, boolean enableX, boolean enableY)
	{
		double rate = Math.pow(deltaZoom, scrollAmount);
		double x = game.getView().getController().getX();
		double y = game.getView().getController().getY();

		x += (point.x - game.canvas.getWidth() / 2) * game.getView().getController().getZoomX();
		y += (point.y - game.canvas.getHeight() / 2) * game.getView().getController().getZoomY();

		if (enableX) applyZoomX(rate);
		if (enableY) applyZoomY(rate);

		x -= (point.x - game.canvas.getWidth() / 2) * game.getView().getController().getZoomX();
		y -= (point.y - game.canvas.getHeight() / 2) * game.getView().getController().getZoomY();

		game.getView().getController().setX(x);
		game.getView().getController().setY(y);

	}

	protected void applyZoomX(double rate)
	{
		ControllerViewXYZoomXY view = game.getView().getController();
		view.setZoomX(view.getZoomX() * rate);
	}

	protected void applyZoomY(double rate)
	{
		ControllerViewXYZoomXY view = game.getView().getController();
		view.setZoomY(view.getZoomY() * rate);
	}

}
