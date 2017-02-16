package mirrg.application.math.wulfenite.core.dialogs;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JFrame;

import mirrg.application.math.wulfenite.core.Wulfenite;
import mirrg.application.math.wulfenite.core.dialog.ParameterBoolean;
import mirrg.application.math.wulfenite.core.dialog.ParameterColor;
import mirrg.application.math.wulfenite.core.dialog.ParameterDouble;
import mirrg.application.math.wulfenite.core.dialog.ParameterInteger;
import mirrg.application.math.wulfenite.core.dialog.ParameterString;
import mirrg.helium.swing.nitrogen.util.TitledGroup;
import mirrg.helium.swing.phosphorus.canvas.game.render.Layer;

public class DialogGrid extends JDialog
{

	private Wulfenite game;

	private ParameterDouble unitX;
	private ParameterString unitNameX;
	private ParameterDouble unitY;
	private ParameterString unitNameY;

	private ParameterBoolean enableGrid;
	private ParameterBoolean enableAxis;
	private ParameterBoolean enableCursor;
	private ParameterBoolean enableOrigin;
	private ParameterBoolean enableExtra;
	private ParameterColor colorGrid;
	private ParameterColor colorAxis;
	private ParameterColor colorCursor;

	private ParameterBoolean enabledCatch;

	private ParameterInteger fontSizeGrid;
	private ParameterInteger fontSizeCursor;
	private ParameterColor fontColorFill;
	private ParameterColor fontColorBorder;
	private ParameterColor fontColorFillStrong;
	private ParameterColor fontColorBorderStrong;
	private ParameterDouble borderWidthGrid;
	private ParameterDouble borderWidthAxis;

	public DialogGrid(Wulfenite game, JFrame frame)
	{
		super(frame, "単位設定");
		this.game = game;

		unitX = new ParameterDouble("実軸単位", () -> game.getModel().grid.unitX, a -> game.getModel().grid.unitX = a, this::pre, this::post);
		unitNameX = new ParameterString("実軸単位名", () -> game.getModel().grid.unitNameX, a -> game.getModel().grid.unitNameX = a, this::pre, this::post);
		unitY = new ParameterDouble("虚軸単位", () -> game.getModel().grid.unitY, a -> game.getModel().grid.unitY = a, this::pre, this::post);
		unitNameY = new ParameterString("虚軸単位名", () -> game.getModel().grid.unitNameY, a -> game.getModel().grid.unitNameY = a, this::pre, this::post);

		enableGrid = new ParameterBoolean("グリッドの表示", () -> game.getModel().grid.enableGrid, a -> game.getModel().grid.enableGrid = a, this::pre, this::post);
		enableAxis = new ParameterBoolean("軸の表示", () -> game.getModel().grid.enableAxis, a -> game.getModel().grid.enableAxis = a, this::pre, this::post);
		enableCursor = new ParameterBoolean("カーソル情報の表示", () -> game.getModel().grid.enableCursor, a -> game.getModel().grid.enableCursor = a, this::pre, this::post);
		enableOrigin = new ParameterBoolean("原点の表示", () -> game.getModel().grid.enableOrigin, a -> game.getModel().grid.enableOrigin = a, this::pre, this::post);
		enableExtra = new ParameterBoolean("追加情報の表示", () -> game.getModel().grid.enableExtra, a -> game.getModel().grid.enableExtra = a, this::pre, this::post);
		colorGrid = new ParameterColor("グリッド色", () -> game.getModel().grid.colorGrid, a -> game.getModel().grid.colorGrid = a, this::pre, this::post);
		colorAxis = new ParameterColor("軸色", () -> game.getModel().grid.colorAxis, a -> game.getModel().grid.colorAxis = a, this::pre, this::post);
		colorCursor = new ParameterColor("カーソル線色", () -> game.getModel().grid.colorCursor, a -> game.getModel().grid.colorCursor = a, this::pre, this::post);

		enabledCatch = new ParameterBoolean("カーソル情報の吸着", () -> game.getModel().grid.enabledCatch, a -> game.getModel().grid.enabledCatch = a, this::pre, this::post);

		fontSizeGrid = new ParameterInteger("グリッドフォントサイズ", () -> game.getModel().grid.fontSizeGrid, a -> game.getModel().grid.fontSizeGrid = a, this::pre, this::post);
		fontSizeCursor = new ParameterInteger("カーソル情報フォントサイズ", () -> game.getModel().grid.fontSizeCursor, a -> game.getModel().grid.fontSizeCursor = a, this::pre, this::post);
		fontColorFill = new ParameterColor("フォント塗りつぶし色", () -> game.getModel().grid.fontColorFill, a -> game.getModel().grid.fontColorFill = a, this::pre, this::post);
		fontColorBorder = new ParameterColor("フォント枠色", () -> game.getModel().grid.fontColorBorder, a -> game.getModel().grid.fontColorBorder = a, this::pre, this::post);
		fontColorFillStrong = new ParameterColor("フォント塗りつぶし色（強調）", () -> game.getModel().grid.fontColorFillStrong, a -> game.getModel().grid.fontColorFillStrong = a, this::pre, this::post);
		fontColorBorderStrong = new ParameterColor("フォント枠色（強調）", () -> game.getModel().grid.fontColorBorderStrong, a -> game.getModel().grid.fontColorBorderStrong = a, this::pre, this::post);
		borderWidthGrid = new ParameterDouble("グリッド線の太さ", () -> game.getModel().grid.borderWidthGrid, a -> game.getModel().grid.borderWidthGrid = a, this::pre, this::post);
		borderWidthAxis = new ParameterDouble("軸の太さ", () -> game.getModel().grid.borderWidthAxis, a -> game.getModel().grid.borderWidthAxis = a, this::pre, this::post);

		add(createBorderPanelUp(
			group("軸の設定", createBorderPanelUp(
				unitX.initRecord(),
				unitNameX.initRecord(),
				unitY.initRecord(),
				unitNameY.initRecord(),
				createBorderPanelLeft(
					createButton("リセット", e -> {
						unitX.set((double) 1);
						unitY.set((double) 1);
						unitNameX.set("");
						unitNameY.set("i");
						post();
					}), createButton("π", e -> {
						unitX.set(Math.PI);
						unitY.set(Math.PI);
						unitNameX.set("π");
						unitNameY.set("π");
						post();
					}),
					null))),
			group("表示", createBorderPanelUp(
				enableGrid.initRecord(),
				enableAxis.initRecord(),
				enableCursor.initRecord(),
				enableOrigin.initRecord(),
				enableExtra.initRecord())),
			group("色", createBorderPanelUp(
				colorGrid.initRecord(),
				colorAxis.initRecord(),
				colorCursor.initRecord(),
				createBorderPanelLeft(
					createButton("白線", e -> {
						colorGrid.set(Color.white);
						colorAxis.set(Color.white);
						colorCursor.set(Color.yellow);
						post();
					}), createButton("黒線", e -> {
						colorGrid.set(Color.black);
						colorAxis.set(Color.black);
						colorCursor.set(Color.blue);
						post();
					}),
					null))),
			enabledCatch.initRecord(),
			group("スタイル", createBorderPanelUp(
				fontSizeGrid.initRecord(),
				fontSizeCursor.initRecord(),
				fontColorFill.initRecord(),
				fontColorBorder.initRecord(),
				fontColorFillStrong.initRecord(),
				fontColorBorderStrong.initRecord(),
				borderWidthGrid.initRecord(),
				borderWidthAxis.initRecord())),
			null));
	}

	private Component group(String title, Component component)
	{
		TitledGroup titledGroup = new TitledGroup(title);
		titledGroup.setLayout(new CardLayout());
		titledGroup.add(component);
		return titledGroup;
	}

	private void pre()
	{

	}

	private void post()
	{
		game.getLayers().forEach(Layer::dirty);
	}

}
