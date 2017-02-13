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
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;

import com.thoughtworks.xstream.XStream;

import mirrg.application.math.wulfenite.script.ModelMapperScript;
import mirrg.helium.standard.hydrogen.struct.Struct1;
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
		DIALOG_UNIT,
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
	public int color; // TODO

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
				'E',
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				e -> getView().getController().setY(getView().getController().getY() - 50 * getView().getController().getZoomY()))));
			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_DOWN,
				"↓(S)",
				"下方向に50ピクセル分スクロールします。",
				'Q',
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				e -> getView().getController().setY(getView().getController().getY() + 50 * getView().getController().getZoomY()))));
			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_LEFT,
				"←(A)",
				"左方向に50ピクセル分スクロールします。",
				'E',
				KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
				e -> getView().getController().setX(getView().getController().getX() - 50 * getView().getController().getZoomX()))));
			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_RIGHT,
				"→(D)",
				"右方向に50ピクセル分スクロールします。",
				'Q',
				KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
				e -> getView().getController().setX(getView().getController().getX() + 50 * getView().getController().getZoomX()))));

			menu.addSeparator();
			menu.add(new JMenuItem(createAction(
				ActionKey.DIALOG_COORDINATE,
				"座標設定...(D)",
				"座標の設定ウィンドウを開きます。",
				'D',
				KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
				e -> openDialogCoordinate())));

			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("色(C)");
			menu.setMnemonic('C');

			JColorChooser colorChooser = new JColorChooser();
			menu.add(new JMenuItem(createAction(
				ActionKey.CHANGE_COLOR_GRID,
				"グリッド色の変更(G)",
				"グリッドの色を変更します。",
				'G',
				null,
				e -> {
					getModel().grid.colorGrid = JColorChooser.showDialog(colorChooser, "グリッドの色の変更", getModel().grid.colorGrid);
				})));
			menu.add(new JMenuItem(createAction(
				ActionKey.CHANGE_COLOR_CURSOR,
				"カーソル色の変更(C)",
				"カーソル線の色を変更します。",
				'C',
				null,
				e -> {
					getModel().grid.colorCursor = JColorChooser.showDialog(colorChooser, "カーソルの色の変更", getModel().grid.colorCursor);
				})));

			menu.addSeparator();
			menu.add(new JMenuItem(createAction(
				ActionKey.CHANGE_COLOR_FUNCTION,
				"色関数の変更(F)",
				"色関数を変更します。",
				'F',
				KeyStroke.getKeyStroke(KeyEvent.VK_C, 0),
				e -> {
					// TODO
					fireChangeFunction(() -> {
						color = (color + 1) % 2;
					});
				})));

			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("表示(V)");
			menu.setMnemonic('V');

			{
				Struct1<JMenuItem> menuItem = new Struct1<>();
				menuItem.x = new JCheckBoxMenuItem(createAction(
					ActionKey.SHOW_GRID,
					"グリッドの表示(G)",
					"グリッド線の表示を切り替えます。",
					'G',
					KeyStroke.getKeyStroke(KeyEvent.VK_G, 0),
					e -> {
						if (!(e.getSource() instanceof JCheckBoxMenuItem)) {
							menuItem.x.setSelected(!menuItem.x.isSelected());
						}
						getModel().grid.enabledGrid = menuItem.x.isSelected();
						getLayers().forEach(Layer::dirty);
					}));
				menuItem.x.setSelected(true);
				menu.add(menuItem.x);
			}
			{
				Struct1<JMenuItem> menuItem = new Struct1<>();
				menuItem.x = new JCheckBoxMenuItem(createAction(
					ActionKey.SHOW_CURSOR_INFO,
					"カーソル情報の表示(C)",
					"カーソル情報の表示を切り替えます。",
					'C',
					KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK),
					e -> {
						if (!(e.getSource() instanceof JCheckBoxMenuItem)) {
							menuItem.x.setSelected(!menuItem.x.isSelected());
						}
						getModel().grid.enabledCursor = menuItem.x.isSelected();
						getLayers().forEach(Layer::dirty);
					}));
				menuItem.x.setSelected(true);
				menu.add(menuItem.x);
			}
			{
				Struct1<JMenuItem> menuItem = new Struct1<>();
				menuItem.x = new JCheckBoxMenuItem(createAction(
					ActionKey.SHOW_EXTRA_INFO,
					"追加情報の表示(E)",
					"関数プラグインによって追加される表示を切り替えます。",
					'E',
					KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.SHIFT_MASK),
					e -> {
						if (!(e.getSource() instanceof JCheckBoxMenuItem)) {
							menuItem.x.setSelected(!menuItem.x.isSelected());
						}
						getModel().grid.enabledExtra = menuItem.x.isSelected();
						getLayers().forEach(Layer::dirty);
					}));
				menuItem.x.setSelected(true);
				menu.add(menuItem.x);
			}

			menu.addSeparator();
			{
				Struct1<JMenuItem> menuItem = new Struct1<>();
				menuItem.x = new JCheckBoxMenuItem(createAction(
					ActionKey.TOGGLE_CATCH,
					"カーソル情報をグリッドに吸着(T)",
					"カーソル情報をグリッドの交点に乗せるか否かを切り替えます。",
					'T',
					KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
					e -> {
						if (!(e.getSource() instanceof JCheckBoxMenuItem)) {
							menuItem.x.setSelected(!menuItem.x.isSelected());
						}
						getModel().grid.enabledCatch = menuItem.x.isSelected();
						getLayers().forEach(Layer::dirty);
					}));
				menuItem.x.setSelected(true);
				menu.add(menuItem.x);
			}

			menu.addSeparator();
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
				ActionKey.DIALOG_UNIT,
				"単位設定...(D)",
				"表示単位の設定ウィンドウを開きます。",
				'D',
				KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0),
				e -> openDialogUnit())));

			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("関数(S)");
			menu.setMnemonic('S');
			menu.add(new JMenuItem(createAction(
				ActionKey.OPEN_CONFIG_DIALOG,
				"設定画面(C)...",
				"関数の設定画面を表示します。",
				'C',
				KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),
				e -> getFunction().toggleDialog())));
			menu.addSeparator();
			menu.add(new JMenuItem(createAction(
				null,
				"Script(1)...",
				"関数を設定します。",
				'1',
				KeyStroke.getKeyStroke(KeyEvent.VK_1, 0),
				e -> setFunction(new ModelMapperScript()))));
			menu.add(new JMenuItem(createAction(
				null,
				"Complex(2)...",
				"関数を設定します。",
				'2',
				KeyStroke.getKeyStroke(KeyEvent.VK_2, 0),
				e -> setFunction(new ModelMapperComplex()))));
			menu.add(new JMenuItem(createAction(
				null,
				"Mandelbrot(3)...",
				"関数を設定します。",
				'3',
				KeyStroke.getKeyStroke(KeyEvent.VK_3, 0),
				e -> setFunction(new ModelMapperMandelbrot()))));
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

	public void openDialogCoordinate()
	{
		JDialog dialog = new DialogCoordinate(this, frame);

		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setLocationByPlatform(true);
		dialog.setVisible(true);
	}

	public void openDialogUnit()
	{
		JDialog dialog = new DialogUnit(this, frame);

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
		action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
		if (acceleratorKey != null) action.putValue(Action.ACCELERATOR_KEY, acceleratorKey);
		actionMap.put(actionKey, action);
		if (acceleratorKey != null) inputMap.put(acceleratorKey, actionKey);

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
