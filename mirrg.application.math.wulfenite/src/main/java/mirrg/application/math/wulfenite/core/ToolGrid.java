package mirrg.application.math.wulfenite.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Optional;

import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.standard.hydrogen.util.HMath;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.swing.phosphorus.canvas.EventPhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.game.existence.Tool;
import mirrg.helium.swing.phosphorus.canvas.game.render.Layer;
import mirrg.helium.swing.phosphorus.canvas.game.render.PointScreen;
import mirrg.helium.swing.phosphorus.canvas.game.render.RectangleCoordinate;

public class ToolGrid extends Tool<Wulfenite>
{

	private static final Font font = new Font("MS Gothic", Font.BOLD, 14);

	private PointScreen point = new PointScreen(0, 0);

	public boolean enabledGrid = true;
	public boolean enabledCursor = true;

	public ToolGrid(Wulfenite wulfenite)
	{
		super(wulfenite);

		registerEvent(EventPhosphorusCanvas.EventMouseMotion.Moved.class, e -> {
			point = new PointScreen(e.event.getPoint());
			dirty(game.layerOverlay);
		});
		registerEvent(EventPhosphorusCanvas.EventMouseMotion.Dragged.class, e -> {
			point = new PointScreen(e.event.getPoint());
			dirty(game.layerOverlay);
		});

		registerGameEvent(EventWulfenite.ChangeFunction.Post.class, e -> {
			dirty(game.layerOverlay);
		});
	}

	// TODO 再編集
	@Override
	public void render(Layer layer)
	{
		if (layer == game.layerOverlay) {
			Graphics2D g = layer.getImageLayer().getGraphics();

			if (!(enabledGrid || enabledCursor)) return;

			// 画面の頂点における数学的座標の取得
			double mx1 = game.getView().getCoordinateX(0);
			double my1 = game.getView().getCoordinateY(0);
			double mx2 = game.getView().getCoordinateX(game.canvas.getWidth());
			double my2 = game.getView().getCoordinateY(game.canvas.getHeight());

			// 数学的座標の反転のチェック
			{
				if (mx1 > mx2) {
					double tmp = mx1;
					mx1 = mx2;
					mx2 = tmp;
				}

				if (my1 > my2) {
					double tmp = my1;
					my1 = my2;
					my2 = tmp;
				}
			}

			// 画面の縦横の数学的大きさ
			final double mwidth = mx2 - mx1;
			final double mheight = my2 - my1;

			// グリッド間隔の数学的距離
			final double gridSpaceW = HMath.nice(mwidth * 0.3);
			final double gridSpaceH = HMath.nice(mheight * 0.3);

			// 有効小数点以下桁数
			final int effectiveDigitW = (int) (-Math.log10(gridSpaceW) + 0.9);
			final int effectiveDigitH = (int) (-Math.log10(gridSpaceH) + 0.9);

			// グリッドの開始数学的座標
			double mgridstartx = Math.ceil(mx1 / gridSpaceW) * gridSpaceW;
			double mgridstarty = Math.ceil(my1 / gridSpaceH) * gridSpaceH;

			// グリッド間隔の描画距離
			final double gridSpaceDispW = game.getView().getScreenX(mx1 + gridSpaceW);
			//final double gridSpaceDispH = cc.getScreenYFromCoordY(my1 + gridSpaceH);

			// グリッドの終了数学的座標
			double mgridendx = mx2;
			double mgridendy = my2;

			// lines
			if (enabledGrid) {
				g.setColor(Color.black);
				for (double mgridx = mgridstartx; mgridx < mgridendx; mgridx += gridSpaceW) {
					int dx = (int) game.getView().getScreenX(mgridx);
					g.drawLine(dx, 0, dx, game.canvas.getHeight());
				}
				for (double mgridy = mgridstarty; mgridy < mgridendy; mgridy += gridSpaceH) {
					// TODO アンダーフロー
					int dy = (int) game.getView().getScreenY(mgridy);
					g.drawLine(0, dy, game.canvas.getWidth(), dy);
				}
			}

			// lines cursor
			if (enabledCursor) {
				g.setColor(Color.blue);

				{
					double mgridx = game.getView().getCoordinateX(point.x);
					int dx = (int) game.getView().getScreenX(mgridx);
					g.drawLine(dx, 0, dx, game.canvas.getHeight());
				}

				{
					double mgridy = game.getView().getCoordinateY(point.y);
					int dy = (int) game.getView().getScreenY(mgridy);
					g.drawLine(0, dy, game.canvas.getWidth(), dy);
				}

			}

			// labels
			if (enabledGrid) {
				// X座標ラベルの描画位置カウンタ
				int drawY = 0;

				for (double mgridx = mgridstartx; mgridx < mgridendx; mgridx += gridSpaceW) {
					int dx = (int) game.getView().getScreenX(mgridx);
					String str = HString.getEffectiveExpression(mgridx, effectiveDigitW);

					g.setFont(font);
					drawBoldString(g, str, dx, (1 + drawY) * g.getFont().getSize(), Color.white, Color.black);

					int textLength = g.getFontMetrics().stringWidth(str);
					drawY = (drawY + 1) % (1 + (int) (textLength / gridSpaceDispW));
				}

				for (double mgridy = mgridstarty; mgridy < mgridendy; mgridy += gridSpaceH) {
					int dy = (int) game.getView().getScreenY(mgridy);
					String str = HString.getEffectiveExpression(mgridy, effectiveDigitH);

					g.setFont(font);
					drawBoldString(g, str, 0, dy + g.getFont().getSize(), Color.white, Color.black);
				}
			}

			// labels cursor
			if (enabledCursor) {

				{
					StructureComplex buffer = new StructureComplex(
						game.getView().getCoordinateX(point.x),
						game.getView().getCoordinateY(point.y));
					String[] valueInformation = game.getFunction().getValueInformation(buffer);

					for (int i = 0; i < valueInformation.length; i++) {
						String str = valueInformation[i];

						g.setFont(font);
						drawBoldString(g, str,
							(int) (point.x + 2),
							(int) (point.y - 2 - g.getFont().getSize() * ((1 + valueInformation.length) - i)),
							Color.white, Color.black);
					}
				}

				{
					double mgridx = game.getView().getCoordinateX(point.x);

					String str = "X: " + HString.getEffectiveExpression(
						mgridx, effectiveDigitW + 4);

					g.setFont(font);
					drawBoldString(g, str,
						(int) (point.x + 2),
						(int) (point.y - 2 - g.getFont().getSize()),
						Color.white, Color.black);
				}

				{
					double mgridy = game.getView().getCoordinateY(point.y);

					String str = "Y: " + HString.getEffectiveExpression(
						mgridy, effectiveDigitH + 4);

					g.setFont(font);
					drawBoldString(g, str,
						(int) (point.x + 2),
						(int) (point.y - 2),
						Color.white, Color.black);
				}

			}

		}
	}

	@Override
	public Optional<RectangleCoordinate> getOpticalBounds(Layer layer)
	{
		if (layer == game.layerOverlay) return Optional.of(game.getView().getCoordinateRectangle());
		return super.getOpticalBounds(layer);
	}

	private void drawBoldString(Graphics2D g, String string, int x, int y, Color inner, Color outer)
	{
		g.setColor(outer);
		g.drawString(string, x - 1, y - 1);
		g.drawString(string, x - 1, y);
		g.drawString(string, x - 1, y + 1);
		g.drawString(string, x, y - 1);
		//
		g.drawString(string, x, y + 1);
		g.drawString(string, x + 1, y - 1);
		g.drawString(string, x + 1, y);
		g.drawString(string, x + 1, y + 1);

		g.setColor(inner);
		g.drawString(string, x, y);
	}

}
