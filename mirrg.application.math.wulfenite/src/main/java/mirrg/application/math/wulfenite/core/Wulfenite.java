package mirrg.application.math.wulfenite.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import com.thoughtworks.xstream.XStream;

import mirrg.helium.swing.phosphorus.canvas.PhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.game.Data;
import mirrg.helium.swing.phosphorus.canvas.game.EventPhosphorusGame;
import mirrg.helium.swing.phosphorus.canvas.game.PhosphorusGame;
import mirrg.helium.swing.phosphorus.canvas.game.render.Layer;
import mirrg.helium.swing.phosphorus.canvas.game.tools.ToolScroll;
import mirrg.helium.swing.phosphorus.canvas.game.tools.ToolZoom;
import mirrg.helium.swing.phosphorus.canvas.game.view.DataViewSkewed;
import mirrg.helium.swing.phosphorus.canvas.game.view.ViewSkewed;

// TODO 点の追加の実装 スクリプトの実装
public class Wulfenite extends PhosphorusGame<Wulfenite>
{

	public final JFrame frame;

	public final Layer layerMath;
	public final Layer layerOverlay;

	public ToolGrid toolGrid;
	public ToolZoom toolZoom;

	public Wulfenite(JFrame frame, PhosphorusCanvas canvas, JMenuBar menuBar)
	{
		super(canvas, createData());
		this.frame = frame;

		{
			JMenu menu = new JMenu("ファイル(F)");
			menu.setMnemonic('F');
			{
				JMenuItem menuItem = new JMenuItem("リセット(R)");
				menuItem.setMnemonic('R');
				menuItem.addActionListener(e -> {
					event().registerRemovable(EventPhosphorusGame.Move.Post.class, e2 -> {
						setData(createData());
						return false;
					});
				});
				menu.add(menuItem);
			}
			{
				JMenuItem menuItem = new JMenuItem("クエリの表示(D)");
				menuItem.setMnemonic('D');
				menuItem.addActionListener(e -> {
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
				});
				menu.add(menuItem);
			}
			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("座標(C)");
			menu.setMnemonic('C');
			{
				JMenuItem menuItem = new JMenuItem("位置と倍率を初期化(R)");
				menuItem.setMnemonic('R');
				menuItem.addActionListener(e -> {
					resetPosition();
				});
				menu.add(menuItem);
			}
			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("表示(V)");
			menu.setMnemonic('V');
			{
				JMenuItem menuItem = new JCheckBoxMenuItem("グリッドを表示(G)");
				menuItem.setMnemonic('G');
				menuItem.setSelected(true);
				menuItem.addActionListener(e -> {
					toolGrid.enabledGrid = menuItem.isSelected();
					getLayers().forEach(Layer::dirty);
				});
				menu.add(menuItem);
			}
			{
				JMenuItem menuItem = new JCheckBoxMenuItem("カーソル情報を表示(C)");
				menuItem.setMnemonic('C');
				menuItem.setSelected(true);
				menuItem.addActionListener(e -> {
					toolGrid.enabledCursor = menuItem.isSelected();
					getLayers().forEach(Layer::dirty);
				});
				menu.add(menuItem);
			}
			menu.addSeparator();
			{
				JMenuItem menuItem = new JMenuItem("水平方向に反転(H)");
				menuItem.setMnemonic('H');
				menuItem.addActionListener(e -> {
					turnHorizontal();
				});
				menu.add(menuItem);
			}
			{
				JMenuItem menuItem = new JMenuItem("垂直方向に反転(V)");
				menuItem.setMnemonic('V');
				menuItem.addActionListener(e -> {
					turnVertical();
				});
				menu.add(menuItem);
			}
			menuBar.add(menu);
		}
		{
			JMenu menu = new JMenu("関数(S)");
			menu.setMnemonic('S');
			{
				JMenuItem menuItem = new JMenuItem("設定画面...(S)");
				menuItem.setMnemonic('S');
				menuItem.addActionListener(e -> {
					getFunction().toggleDialog();
				});
				menu.add(menuItem);
			}
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
		addTool(new ToolWulfenite(this));
		addTool(new ToolWulfeniteScrollSaver(this));

	}

	public XStream getXStream()
	{
		XStream xStream = new XStream();
		xStream.autodetectAnnotations(true);
		return xStream;
	}

	@Override
	public DataWulfenite getData()
	{
		return (DataWulfenite) super.getData();
	}

	@Override
	public synchronized void setData(Data<Wulfenite> data)
	{
		DataWulfenite data2 = (DataWulfenite) data;
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
			getData().wulfeniteFunction.dispose();
			getData().wulfeniteFunction = wulfeniteFunction;
			getData().wulfeniteFunction.initialize(this);
		});
	}

	public void invokeLater(Runnable runnable)
	{
		event().registerRemovable(EventPhosphorusGame.Move.Post.class, e2 -> {
			runnable.run();
			return false;
		});
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

	@SuppressWarnings("unchecked")
	public void setXML(String xml)
	{
		setData((Data<Wulfenite>) getXStream().fromXML(xml));
	}

	public void fireChangeFunction(Runnable inner)
	{
		event().post(new EventWulfenite.ChangeFunction.Pre());
		inner.run();
		event().post(new EventWulfenite.ChangeFunction.Post());
	}

}
