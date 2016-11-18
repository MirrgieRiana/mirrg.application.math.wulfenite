package mirrg.application.math.wulfenite.core;

import com.sun.glass.events.KeyEvent;

import mirrg.application.math.wulfenite.script.WulfeniteFunctionScript;
import mirrg.helium.swing.phosphorus.canvas.EventPhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Tool;

public class ToolWulfenite extends Tool<Wulfenite>
{

	private String xml;

	public ToolWulfenite(Wulfenite game)
	{
		super(game);

		registerEvent(EventPhosphorusCanvas.EventKey.Pressed.class, e -> {

			// 画面操作
			if (e.event.getKeyCode() == KeyEvent.VK_ADD) {
				game.toolZoom.doZoom(-1);
			} else if (e.event.getKeyCode() == KeyEvent.VK_SUBTRACT) {
				game.toolZoom.doZoom(1);
			} else if (e.event.getKeyCode() == KeyEvent.VK_UP) {
				game.getView().setY(game.getView().getY() - 30 * game.getView().getZoomY());
			} else if (e.event.getKeyCode() == KeyEvent.VK_DOWN) {
				game.getView().setY(game.getView().getY() + 30 * game.getView().getZoomY());
			} else if (e.event.getKeyCode() == KeyEvent.VK_LEFT) {
				game.getView().setX(game.getView().getX() - 30 * game.getView().getZoomX());
			} else if (e.event.getKeyCode() == KeyEvent.VK_RIGHT) {
				game.getView().setX(game.getView().getX() + 30 * game.getView().getZoomX());
			} else if (e.event.getKeyCode() == KeyEvent.VK_Q) {
				game.turnHorizontal();
			} else if (e.event.getKeyCode() == KeyEvent.VK_W) {
				game.turnVertical();
			} else if (e.event.getKeyCode() == KeyEvent.VK_0) {
				game.resetPosition();
			}

			// セーブロード
			if (e.event.getKeyCode() == KeyEvent.VK_F1) {
				xml = game.getXML();
				System.out.println(xml);
			} else if (e.event.getKeyCode() == KeyEvent.VK_F2) {
				if (xml != null) game.invokeLater(() -> game.setXML(xml));
			}

			// 関数制御
			if (e.event.getKeyCode() == KeyEvent.VK_1) {
				game.setFunction(new WulfeniteFunctionMandelbrot(game));
			} else if (e.event.getKeyCode() == KeyEvent.VK_2) {
				game.setFunction(new WulfeniteFunctionComplex(game));
			} else if (e.event.getKeyCode() == KeyEvent.VK_3) {
				game.setFunction(new WulfeniteFunctionScript(game));
			}

		});

	}

}
