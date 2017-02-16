package mirrg.application.math.wulfenite.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;

import com.thoughtworks.xstream.XStream;

import mirrg.application.math.wulfenite.core.dialogs.DialogCoordinate;
import mirrg.application.math.wulfenite.core.dialogs.DialogGrid;
import mirrg.application.math.wulfenite.script.ModelMapperScript;
import mirrg.helium.swing.nitrogen.wrapper.artifacts.logging.HLog;
import mirrg.helium.swing.phosphorus.canvas.PhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.game.EventGamePhosphorus;
import mirrg.helium.swing.phosphorus.canvas.game.GamePhosphorus;
import mirrg.helium.swing.phosphorus.canvas.game.entity.ToolScroll;
import mirrg.helium.swing.phosphorus.canvas.game.render.Layer;
import mirrg.helium.swing.phosphorus.canvas.game.view.ModelViewXYZoomXY;

// TODO クリプトが無限ループしたときの処理
public class Wulfenite extends GamePhosphorus<Wulfenite, ModelWulfenite, ModelViewXYZoomXY>
{

	public static enum ActionKey
	{
		RESET,
		RESET_COORDINATE,
		RESET_ASPECT,
		SCREEN_SHOT,
		SCREEN_SHOT_WITHOUT_UI,
		TEMPORARY_SAVE,
		TEMPORARY_LOAD,
		OPEN_CONFIG_DIALOG,
		OPEN_QUERY_DIALOG,
		MIRROR_VERTICAL,
		MIRROR_HORIZONTAL,
		DIALOG_COORDINATE,
		DIALOG_GRID,
		DIALOG_COLOR,
		ZOOM_IN,
		ZOOM_IN_X,
		ZOOM_IN_Y,
		ZOOM_OUT,
		ZOOM_OUT_X,
		ZOOM_OUT_Y,
		MOVE_UP,
		MOVE_DOWN,
		MOVE_LEFT,
		MOVE_RIGHT,
		SHOW_GRID,
		SHOW_CURSOR_INFO,
		SHOW_EXTRA_INFO,
		TOGGLE_CATCH,
		CHANGE_COLOR_GRID,
		CHANGE_COLOR_CURSOR,
		CHANGE_COLOR_FUNCTION,
	}

	public final FrameWulfenite frame;

	public final Layer layerMath;
	public final Layer layerOverlay;

	public ToolZoomXY toolZoom;

	public final ActionMap actionMap;
	public final InputMap inputMap;

	private String xml;

	public Wulfenite(FrameWulfenite frame, PhosphorusCanvas canvas, JMenuBar menuBar)
	{
		super(canvas);
		this.frame = frame;

		actionMap = frame.getRootPane().getActionMap();
		inputMap = frame.getRootPane().getInputMap();

		{
			JMenu menu = new JMenu("ファイル(F)");
			menu.setMnemonic('F');
			menu.add(new JMenuItem(createAction(
				ActionKey.RESET,
				"リセット(R)",
				"関数の設定と表示位置を初期化します。",
				'R',
				KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK | Event.SHIFT_MASK),
				e -> {
					event().registerRemovable(EventGamePhosphorus.Move.Post.class, e2 -> {
						setModel(createDefaultData());
						return false;
					});
				})));
			menu.addSeparator();

			menu.add(new JMenuItem(createAction(
				ActionKey.TEMPORARY_SAVE,
				"一時保存(S)",
				"現在のアプリケーションの状態をメモリ上にバックアップします。",
				'S',
				KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
				e -> {
					xml = getXML();
					System.out.println(xml);
				})));
			menu.add(new JMenuItem(createAction(
				ActionKey.TEMPORARY_LOAD,
				"一時保存から復元(L)",
				"バックアップからデータを読み込みます。",
				'L',
				KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
				e -> {
					if (xml != null) invokeLater(() -> setXML(xml));
				})));

			menu.add(new JMenuItem(createAction(
				ActionKey.OPEN_QUERY_DIALOG,
				"クエリの表示(Q)",
				"現在のアプリケーションの状態を表すXML表現を表示します。",
				'Q',
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0),
				e -> {
					// TODO
					JDialog dialog = new JDialog(frame, "クエリ");

					JTextPane textPane = new JTextPane();
					textPane.setText(getXML());
					textPane.setPreferredSize(new Dimension(300, 300));
					dialog.add(textPane);

					JButton button = new JButton("セット");
					button.setDefaultCapable(true);
					button.addActionListener(e2 -> invokeLater(() -> setXML(textPane.getText())));
					dialog.add(button, BorderLayout.SOUTH);

					dialog.pack();
					dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					dialog.setLocationByPlatform(true);
					dialog.setVisible(true);
				})));

			menu.addSeparator();
			menu.add(new JMenuItem(createAction(
				ActionKey.SCREEN_SHOT,
				"表示画面のエクスポート(P)",
				"現在の画面を保存します。",
				'P',
				KeyStroke.getKeyStroke(KeyEvent.VK_P, 0),
				e -> {
					doScreenShot(false);
				})));
			menu.add(new JMenuItem(createAction(
				ActionKey.SCREEN_SHOT_WITHOUT_UI,
				"数学レイヤーのエクスポート(M)",
				"現在の数学レイヤーを保存します。",
				'M',
				KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.SHIFT_DOWN_MASK),
				e -> {
					doScreenShot(true);
				})));

			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("座標(P)");
			menu.setMnemonic('P');
			menu.add(new JMenuItem(createAction(
				ActionKey.RESET_COORDINATE,
				"位置と倍率の初期化(R)",
				"表示位置と表示倍率を初期化します。",
				'R',
				KeyStroke.getKeyStroke(KeyEvent.VK_R, 0),
				e -> resetPosition())));
			menu.add(new JMenuItem(createAction(
				ActionKey.RESET_ASPECT,
				"縦横比の初期化(T)",
				"表示倍率の縦横比を初期化します。",
				'T',
				KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK),
				e -> resetAspect())));
			menu.addSeparator();
			{
				menu.add(new JMenuItem(createAction(
					ActionKey.ZOOM_IN,
					"拡大(E)",
					"ホイール1段階分ズームインします。",
					'E',
					KeyStroke.getKeyStroke(KeyEvent.VK_X, 0),
					e -> toolZoom.doZoom(-1))));
				menu.add(new JMenuItem(createAction(
					ActionKey.ZOOM_IN_X,
					"実軸のみ拡大(Z)",
					"実軸のみホイール1段階分ズームインします。",
					'Z',
					KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK),
					e -> toolZoom.doZoom(-1, true, false))));
				menu.add(new JMenuItem(createAction(
					ActionKey.ZOOM_IN_Y,
					"虚軸のみ拡大(X)",
					"虚軸のみホイール1段階分ズームインします。",
					'X',
					KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.SHIFT_DOWN_MASK),
					e -> toolZoom.doZoom(-1, false, true))));
			}
			{
				menu.add(new JMenuItem(createAction(
					ActionKey.ZOOM_OUT,
					"縮小(Q)",
					"ホイール1段階分ズームアウトします。",
					'Q',
					KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0),
					e -> toolZoom.doZoom(1))));
				menu.add(new JMenuItem(createAction(
					ActionKey.ZOOM_OUT_X,
					"実軸のみ縮小(C)",
					"実軸のみホイール1段階分ズームアウトします。",
					'C',
					KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK),
					e -> toolZoom.doZoom(1, true, false))));
				menu.add(new JMenuItem(createAction(
					ActionKey.ZOOM_OUT_Y,
					"虚軸のみ縮小(V)",
					"虚軸のみホイール1段階分ズームアウトします。",
					'V',
					KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.SHIFT_DOWN_MASK),
					e -> toolZoom.doZoom(1, false, true))));
			}

			menu.addSeparator();
			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_UP,
				"↑(W)",
				"上方向に50ピクセル分スクロールします。",
				'W',
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				e -> getView().getController().setY(getView().getController().getY() - 50 * getView().getController().getZoomY()))));
			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_DOWN,
				"↓(S)",
				"下方向に50ピクセル分スクロールします。",
				'S',
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				e -> getView().getController().setY(getView().getController().getY() + 50 * getView().getController().getZoomY()))));
			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_LEFT,
				"←(A)",
				"左方向に50ピクセル分スクロールします。",
				'A',
				KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
				e -> getView().getController().setX(getView().getController().getX() - 50 * getView().getController().getZoomX()))));
			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_RIGHT,
				"→(D)",
				"右方向に50ピクセル分スクロールします。",
				'D',
				KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
				e -> getView().getController().setX(getView().getController().getX() + 50 * getView().getController().getZoomX()))));

			menu.addSeparator();
			menu.add(new JMenuItem(createAction(
				ActionKey.DIALOG_COORDINATE,
				"座標設定...(D)",
				"座標の設定ウィンドウを開きます。",
				'D',
				KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),
				e -> openDialog(DialogCoordinate::new))));

			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("関数(S)");
			menu.setMnemonic('S');

			menu.add(new JMenuItem(createAction(
				null,
				"Script(1)",
				null,
				'1',
				null,
				e -> setFunction(new ModelMapperScript()))));
			menu.add(new JMenuItem(createAction(
				null,
				"Complex(2)",
				null,
				'2',
				null,
				e -> setFunction(new ModelMapperComplex()))));
			menu.add(new JMenuItem(createAction(
				null,
				"Mandelbrot(3)",
				null,
				'3',
				null,
				e -> setFunction(new ModelMapperMandelbrot()))));

			menu.addSeparator();
			menu.add(new JMenuItem(createAction(
				ActionKey.OPEN_CONFIG_DIALOG,
				"関数設定(D)...",
				"関数の設定画面を表示します。",
				'D',
				KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0),
				e -> getFunction().toggleDialog())));

			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("色関数(C)");
			menu.setMnemonic('C');

			menu.add(new JMenuItem(createAction(
				null,
				"Default(1)",
				null,
				'1',
				null,
				e -> setColorMapper(new ModelColorMapperDefault()))));
			menu.add(new JMenuItem(createAction(
				null,
				"ZeroPoint(2)",
				null,
				'2',
				null,
				e -> setColorMapper(new ModelColorMapperZeroPoint()))));
			menu.add(new JMenuItem(createAction(
				null,
				"ZeroPointGray(3)",
				null,
				'3',
				null,
				e -> setColorMapper(new ModelColorMapperZeroPointGray()))));

			menu.addSeparator();
			menu.add(new JMenuItem(createAction(
				ActionKey.DIALOG_COLOR,
				"色関数設定...(D)",
				"色関数の設定ウィンドウを開きます。",
				'D',
				KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
				e -> {
					JDialog dialog = getModel().colorMapper.getController().createDialog(frame);
					if (dialog != null) openDialog(dialog);
				})));

			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("グリッド(G)");
			menu.setMnemonic('G');

			menu.add(new JMenuItem(createAction(
				ActionKey.MIRROR_HORIZONTAL,
				"水平方向に反転(H)",
				"左右の軸を反転します。",
				'H',
				KeyStroke.getKeyStroke(KeyEvent.VK_H, 0),
				e -> turnHorizontal())));
			menu.add(new JMenuItem(createAction(
				ActionKey.MIRROR_VERTICAL,
				"垂直方向に反転(V)",
				"上下の軸を反転します。",
				'V',
				KeyStroke.getKeyStroke(KeyEvent.VK_V, 0),
				e -> turnVertical())));

			menu.addSeparator();
			menu.add(new JMenuItem(createAction(
				ActionKey.DIALOG_GRID,
				"グリッド設定...(D)",
				"グリッドの設定ウィンドウを開きます。",
				'D',
				KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0),
				e -> openDialog(DialogGrid::new))));

			menuBar.add(menu);
		}

		canvas.setTransferHandler(new TransferHandler() {

			@Override
			public boolean canImport(TransferSupport transferSupport)
			{
				if (!transferSupport.isDrop()) return false;
				return transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
			}

			@SuppressWarnings("unchecked")
			@Override
			public boolean importData(TransferSupport transferSupport)
			{
				if (!canImport(transferSupport)) return false;

				// ファイル名取得
				Transferable transferable = transferSupport.getTransferable();
				List<File> files;
				try {
					files = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
				} catch (UnsupportedFlavorException | IOException e) {
					HLog.processException(e);
					return false;
				}
				if (files.size() != 1) return false;
				File file = files.get(0);

				// PNG画像なら同名XMLを参照
				String path = file.getAbsolutePath();
				if (path.endsWith(".png")) {
					path = path.substring(0, path.length() - 4) + ".xml";
				}
				file = new File(path);
				if (!file.isFile()) return false;

				// XMLコード読み取り
				String xml;
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf8"));
					xml = in.lines()
						.collect(Collectors.joining(System.lineSeparator()));
					in.close();
				} catch (IOException e) {
					HLog.processException(e);
					return true;
				}

				// セット
				setXML(xml);

				return true;
			}
		});

		layerMath = createLayer();
		layerMath.setAutoClear(false);
		addLayer(layerMath);
		addLayer(layerOverlay = createLayer());
	}

	public void doScreenShot(boolean onlyMathLayer)
	{
		BufferedImage image = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();

		if (onlyMathLayer) {
			layerMath.paint(graphics, () -> {
				of(this::getEntities)
					.sorted((a, b) -> (int) Math.signum(a.getZOrder() - b.getZOrder()))
					.forEach(e -> e.render(layerMath));
			});
		} else {
			getLayers().forEach(l -> {
				l.paint(graphics, () -> {
					of(this::getEntities)
						.sorted((a, b) -> (int) Math.signum(a.getZOrder() - b.getZOrder()))
						.forEach(e -> e.render(l));
				});
			});
		}

		//

		String filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss-SSS"));
		try {
			ImageIO.write(image, "png", new File(filename + ".png"));
		} catch (IOException e) {
			HLog.processException(e);
		}

		//

		try {
			PrintStream out = new PrintStream(new File(filename + ".xml"), "utf8");
			out.print(getXML());
			out.close();
		} catch (FileNotFoundException e) {
			HLog.processException(e);
		} catch (UnsupportedEncodingException e) {
			HLog.processException(e);
		}

	}

	public void openDialog(BiFunction<Wulfenite, FrameWulfenite, JDialog> constructor)
	{
		openDialog(constructor.apply(this, frame));
	}

	public void openDialog(JDialog dialog)
	{
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setLocationByPlatform(true);
		dialog.setVisible(true);
	}

	public void init()
	{
		addTool(new ToolScroll(this, MouseEvent.BUTTON2));
		addTool(toolZoom = new ToolZoomXY(this));
		addTool(new ToolWulfenitePainter(this, 45));
		addTool(new ToolWulfeniteScrollSaver(this));
	}

	public Action createAction(
		ActionKey actionKey,
		String name,
		String shortDescription,
		char mnemonicKey,
		KeyStroke acceleratorKey,
		ActionListener listener)
	{
		AbstractAction action = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				listener.actionPerformed(e);
			}

		};
		action.putValue(Action.NAME, name);
		action.putValue(Action.MNEMONIC_KEY, Integer.valueOf(mnemonicKey));
		if (shortDescription != null) action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
		if (acceleratorKey != null) action.putValue(Action.ACCELERATOR_KEY, acceleratorKey);
		if (actionKey != null) actionMap.put(actionKey, action);
		if (acceleratorKey != null && actionKey != null) inputMap.put(acceleratorKey, actionKey);

		return action;
	}

	@Override
	public XStream getXStream()
	{
		XStream xStream = new XStream();
		xStream.autodetectAnnotations(true);
		return xStream;
	}

	@Override
	public synchronized void setModel(ModelWulfenite data)
	{
		fireChangeFunction(() -> super.setModel(data));
	}

	public IMapper getFunction()
	{
		return (IMapper) getModel().mapper.getController();
	}

	public void setFunction(ModelMapperBase wulfeniteFunction)
	{
		fireChangeFunction(() -> {
			ModelMapperBase tmp = getModel().mapper;
			wulfeniteFunction.initialize(this);
			getModel().mapper = wulfeniteFunction;
			tmp.dispose();
		});
	}

	public void setColorMapper(ModelColorMapperBase modelColorMapper)
	{
		fireChangeFunction(() -> {
			ModelColorMapperBase tmp = getModel().colorMapper;
			modelColorMapper.initialize(this);
			getModel().colorMapper = modelColorMapper;
			tmp.dispose();
		});
	}

	public void invokeLater(Runnable runnable)
	{
		event().registerRemovable(EventGamePhosphorus.Move.Post.class, e2 -> {
			runnable.run();
			return false;
		});
	}

	public static ModelWulfenite createDefaultData()
	{
		return ModelWulfenite.create();
	}

	public void turnVertical()
	{
		getView().getController().setZoomY(-getView().getController().getZoomY());
	}

	public void turnHorizontal()
	{
		getView().getController().setZoomX(-getView().getController().getZoomX());
	}

	public void resetPosition()
	{
		getView().getController().setX(0);
		getView().getController().setY(0);
		getView().getController().setZoomX(0.01);
		getView().getController().setZoomY(-0.01);
	}

	public void resetAspect()
	{
		double x = Math.signum(getView().getController().getZoomX());
		double y = Math.signum(getView().getController().getZoomY());
		double zoom = (Math.abs(getView().getController().getZoomX())
			+ Math.abs(getView().getController().getZoomY())) / 2;
		getView().getController().setZoomX(x * zoom);
		getView().getController().setZoomY(y * zoom);
	}

	public String getXML()
	{
		return getXStream().toXML(getModel());
	}

	public void setXML(String xml)
	{
		setModel((ModelWulfenite) getXStream().fromXML(xml));
	}

	public void fireChangeFunction(Runnable inner)
	{
		event().post(new EventWulfenite.ChangeFunction.Pre());
		inner.run();
		event().post(new EventWulfenite.ChangeFunction.Post());
	}

}
