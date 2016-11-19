package mirrg.application.math.wulfenite;

import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import mirrg.application.math.wulfenite.core.Wulfenite;
import mirrg.helium.swing.nitrogen.util.HSwing;
import mirrg.helium.swing.phosphorus.canvas.game.IGame;
import mirrg.helium.swing.phosphorus.canvas.util.FrameCanvas;
import mirrg.helium.swing.phosphorus.canvas.util.IntervalThread;

public class Main
{

	public static void main(String[] args)
	{
		HSwing.setWindowsLookAndFeel();

		FrameCanvas frame = new FrameCanvas(600, 600);

		frame.setTitle("Wulfenite");
		{
			ImageIcon imageIcon = new ImageIcon(Main.class.getResource("icon.png"));
			frame.setIconImage(imageIcon.getImage());
		}

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		IGame game = new Wulfenite(frame, frame.canvas, menuBar);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);

		new IntervalThread(50, () -> {
			game.render(frame.canvas.getLayer().getGraphics());
			frame.canvas.repaint();
		}).start();
		new IntervalThread(20, game::move).start();
	}

}
