package mirrg.application.math.wulfenite.core;

import java.awt.event.KeyEvent;

import javax.swing.JRootPane;

import mirrg.helium.swing.phosphorus.canvas.EventPhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.util.FrameCanvas;

public class FrameWulfenite extends FrameCanvas
{

	public FrameWulfenite(int width, int height)
	{
		super(width, height);
	}

	@Override
	protected JRootPane createRootPane()
	{
		JRootPane rp = new JRootPane() {

			@Override
			protected void processKeyEvent(KeyEvent e)
			{
				switch (e.getID()) {
					case KeyEvent.KEY_TYPED:
						canvas.event().post(new EventPhosphorusCanvas.EventKey.Typed(e));
						break;
					case KeyEvent.KEY_PRESSED:
						canvas.event().post(new EventPhosphorusCanvas.EventKey.Pressed(e));
						break;
					case KeyEvent.KEY_RELEASED:
						canvas.event().post(new EventPhosphorusCanvas.EventKey.Released(e));
						break;
				}
				super.processKeyEvent(e);
			}

		};
		rp.setOpaque(true);
		return rp;
	}

}
