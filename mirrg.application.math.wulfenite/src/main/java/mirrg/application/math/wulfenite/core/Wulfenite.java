package mirrg.application.math.wulfenite.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import com.thoughtworks.xstream.XStream;

import mirrg.application.math.wulfenite.script.DataWulfeniteFunctionScript;
import mirrg.helium.standard.hydrogen.struct.Struct1;
import mirrg.helium.swing.phosphorus.canvas.PhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.game.EventPhosphorusGame;
import mirrg.helium.swing.phosphorus.canvas.game.PhosphorusGame;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Existence;
import mirrg.helium.swing.phosphorus.canvas.game.render.Layer;
import mirrg.helium.swing.phosphorus.canvas.game.tools.ToolScroll;
import mirrg.helium.swing.phosphorus.canvas.game.tools.ToolZoom;
import mirrg.helium.swing.phosphorus.canvas.game.view.DataViewSkewed;
import mirrg.helium.swing.phosphorus.canvas.game.view.ViewSkewed;

// TODO 点の追加の実装 スクリプトが無限ループしたときの処理
public class Wulfenite extends PhosphorusGame<Wulfenite, DataWulfenite>
{

	public static enum ActionKey
	{
		RESET,
		RESET_COORDINATE,
		TEMPORARY_SAVE,
		TEMPORARY_LOAD,
		OPEN_CONFIG_DIALOG,
		OPEN_QUERY_DIALOG,
		MIRROR_VERTICAL,
		MIRROR_HORIZONTAL,
		ZOOM_IN,
		ZOOM_OUT,
		MOVE_UP,
		MOVE_DOWN,
		MOVE_LEFT,
		MOVE_RIGHT,
		SHOW_GRID,
		SHOW_CURSOR_INFO,
	}

	public final JFrame frame;

	public final Layer layerMath;
	public final Layer layerOverlay;

	public ToolGrid toolGrid;
	public ToolZoom toolZoom;

	public final ActionMap actionMap;
	public final InputMap inputMap;

	private String xml;

	public Wulfenite(JFrame frame, PhosphorusCanvas canvas, JMenuBar menuBar)
	{
		super(canvas, createData());
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
					event().registerRemovable(EventPhosphorusGame.Move.Post.class, e2 -> {
						setData(createData());
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
			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("座標(C)");
			menu.setMnemonic('C');
			menu.add(new JMenuItem(createAction(
				ActionKey.RESET_COORDINATE,
				"位置と倍率の初期化(R)",
				"表示位置を初期化します。",
				'R',
				KeyStroke.getKeyStroke(KeyEvent.VK_R, 0),
				e -> resetPosition())));
			menu.addSeparator();
			menu.add(new JMenuItem(createAction(
				ActionKey.ZOOM_IN,
				"拡大(E)",
				"ホイール1段階分ズームインします。",
				'E',
				KeyStroke.getKeyStroke(KeyEvent.VK_X, 0),
				e -> toolZoom.doZoom(-1))));
			menu.add(new JMenuItem(createAction(
				ActionKey.ZOOM_OUT,
				"縮小(Q)",
				"ホイール1段階分ズームアウトします。",
				'Q',
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0),
				e -> toolZoom.doZoom(1))));

			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_UP,
				"↑(W)",
				"上方向に50ピクセル分スクロールします。",
				'E',
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				e -> getView().setY(getView().getY() - 50 * getView().getZoomY()))));
			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_DOWN,
				"↓(S)",
				"下方向に50ピクセル分スクロールします。",
				'Q',
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				e -> getView().setY(getView().getY() + 50 * getView().getZoomY()))));
			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_LEFT,
				"←(A)",
				"左方向に50ピクセル分スクロールします。",
				'E',
				KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
				e -> getView().setX(getView().getX() - 50 * getView().getZoomX()))));
			menu.add(new JMenuItem(createAction(
				ActionKey.MOVE_RIGHT,
				"→(D)",
				"右方向に50ピクセル分スクロールします。",
				'Q',
				KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
				e -> getView().setX(getView().getX() + 50 * getView().getZoomX()))));

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
						toolGrid.enabledGrid = menuItem.x.isSelected();
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
						toolGrid.enabledCursor = menuItem.x.isSelected();
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
				e -> setFunction(new DataWulfeniteFunctionScript()))));
			menu.add(new JMenuItem(createAction(
				null,
				"Complex(2)...",
				"関数を設定します。",
				'2',
				KeyStroke.getKeyStroke(KeyEvent.VK_2, 0),
				e -> setFunction(new DataWulfeniteFunctionComplex()))));
			menu.add(new JMenuItem(createAction(
				null,
				"Mandelbrot(3)...",
				"関数を設定します。",
				'3',
				KeyStroke.getKeyStroke(KeyEvent.VK_3, 0),
				e -> setFunction(new DataWulfeniteFunctionMandelbrot()))));
			menuBar.add(menu);
		}

		layerMath = createLayer();
		layerMath.setAutoClear(false);
		addLayer(layerMath);
		addLayer(layerOverlay = createLayer());

		addTool(new ToolScroll(this, MouseEvent.BUTTON2));
		addTool(toolZoom = new ToolZoom(this));
		addTool(new ToolWulfenitePainter(this, 45));
		addTool(toolGrid = new ToolGrid(this));
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
		action.putValue(Action.ACCELERATOR_KEY, acceleratorKey);
		actionMap.put(actionKey, action);
		inputMap.put(acceleratorKey, actionKey);

		return action;
	}

	public XStream getXStream()
	{
		XStream xStream = new XStream();
		xStream.autodetectAnnotations(true);
		return xStream;
	}

	@Override
	public synchronized void setData(DataWulfenite data)
	{
		DataWulfenite data2 = data;
		fireChangeFunction(() -> {
			super.setData(data2);
		});
	}

	public IEntityWulfeniteFunction getFunction()
	{
		return (IEntityWulfeniteFunction) getData().wulfeniteFunction.getEntity();
	}

	public void setFunction(DataWulfeniteFunctionBase wulfeniteFunction)
	{
		fireChangeFunction(() -> {
			DataWulfeniteFunctionBase tmp = getData().wulfeniteFunction;
			wulfeniteFunction.initialize(this);
			getData().wulfeniteFunction = wulfeniteFunction;
			tmp.dispose();
		});
	}

	public void invokeLater(Runnable runnable)
	{
		event().registerRemovable(EventPhosphorusGame.Move.Post.class, e2 -> {
			runnable.run();
			return false;
		});
	}

	@Override
	public void getExistences(Consumer<Existence<? super Wulfenite>> consumer)
	{
		super.getExistences(consumer);
		consumer.accept(getData().wulfeniteFunction.getEntity());
	}

	public static DataWulfenite createData()
	{
		DataWulfenite data = new DataWulfenite();
		{
			DataViewSkewed dataViewSkewed = new DataViewSkewed();
			dataViewSkewed.zoomX = 0.01;
			dataViewSkewed.zoomY = -0.01;
			data.view = dataViewSkewed;
		}
		{
			DataWulfeniteFunctionScript dataWulfeniteFunctionScript = new DataWulfeniteFunctionScript();
			dataWulfeniteFunctionScript.source = "(x + 1 + 1i) * (x - 1 - 1i) / (x - 1 + 1i) / (x + 1 - 1i)";
			data.wulfeniteFunction = dataWulfeniteFunctionScript;
		}
		return data;
	}

	@Override
	public ViewSkewed getView()
	{
		return (ViewSkewed) super.getView();
	}

	public void turnVertical()
	{
		getView().setZoomY(-getView().getZoomY());
	}

	public void turnHorizontal()
	{
		getView().setZoomX(-getView().getZoomX());
	}

	public void resetPosition()
	{
		getView().setX(0);
		getView().setY(0);
		getView().setZoomX(0.01);
		getView().setZoomY(-0.01);
	}

	public String getXML()
	{
		return getXStream().toXML(getData());
	}

	public void setXML(String xml)
	{
		setData((DataWulfenite) getXStream().fromXML(xml));
	}

	public void fireChangeFunction(Runnable inner)
	{
		event().post(new EventWulfenite.ChangeFunction.Pre());
		inner.run();
		event().post(new EventWulfenite.ChangeFunction.Post());
	}

}
