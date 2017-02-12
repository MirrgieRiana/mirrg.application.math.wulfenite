package mirrg.application.math.wulfenite.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Optional;

import mirrg.helium.math.hydrogen.complex.StructureComplex;
import mirrg.helium.standard.hydrogen.struct.Tuple;
import mirrg.helium.standard.hydrogen.util.HMath;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.swing.phosphorus.canvas.EventPhosphorusCanvas;
import mirrg.helium.swing.phosphorus.canvas.game.entity.ModelEntity.Entity;
import mirrg.helium.swing.phosphorus.canvas.game.render.Layer;
import mirrg.helium.swing.phosphorus.canvas.game.render.PointScreen;
import mirrg.helium.swing.phosphorus.canvas.game.render.RectangleCoordinate;

public class ToolGrid extends Entity<Wulfenite>
{

	private static final Font font = new Font("MS Gothic", Font.BOLD, 14);

	private PointScreen point = new PointScreen(0, 0);
	private boolean isControl = false;

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
		registerEvent(EventPhosphorusCanvas.EventKey.Pressed.class, e -> {
			isControl = true;
			dirty(game.layerOverlay);
		});
		registerEvent(EventPhosphorusCanvas.EventKey.Released.class, e -> {
			isControl = false;
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
			double mx1 = game.getView().getController().getCoordinateX(0);
			double my1 = game.getView().getController().getCoordinateY(0);
			double mx2 = game.getView().getController().getCoordinateX(game.canvas.getWidth());
			double my2 = game.getView().getController().getCoordinateY(game.canvas.getHeight());

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
			final double gridSpaceDispW = game.getView().getController().getScreenX(mx1 + gridSpaceW);
			//final double gridSpaceDispH = cc.getScreenYFromCoordY(my1 + gridSpaceH);

			// グリッドの終了数学的座標
			double mgridendx = mx2;
			double mgridendy = my2;

			// グリッドの数学的座標
			ArrayList<Double> linesX = new ArrayList<>();
			for (double x = mgridstartx; x < mgridendx; x += gridSpaceW) {
				linesX.add(x);
			}
			ArrayList<Double> linesY = new ArrayList<>();
			for (double y = mgridstarty; y < mgridendy; y += gridSpaceH) {
				linesY.add(y);
			}

			// カーソルの数学的座標
			double mgridx = game.getView().getController().getCoordinateX(point.x);
			double mgridy = game.getView().getController().getCoordinateY(point.y);
			if (isControl) {
				// カーソル情報の座標をグリッドのある座標に固定

				double mgridx2 = mgridx;
				double mgridy2 = mgridy;

				Optional<Double> x = linesX.stream()
					.map(a -> new Tuple<>(a, Math.abs(mgridx2 - a)))
					.min((a, b) -> (int) Math.signum(a.getY() - b.getY()))
					.map(a -> a.getX());
				if (x.isPresent()) mgridx = x.get();

				Optional<Double> y = linesY.stream()
					.map(a -> new Tuple<>(a, Math.abs(mgridy2 - a)))
					.min((a, b) -> (int) Math.signum(a.getY() - b.getY()))
					.map(a -> a.getX());
				if (y.isPresent()) mgridy = y.get();
			}

			// lines
			if (enabledGrid) {
				// TODO アンダーフロー
				g.setColor(Color.black);
				for (double x : linesX) {
					int dx = (int) game.getView().getController().getScreenX(x);
					g.drawLine(dx, 0, dx, game.canvas.getHeight());
				}
				for (double y : linesY) {
					int dy = (int) game.getView().getController().getScreenY(y);
					g.drawLine(0, dy, game.canvas.getWidth(), dy);
				}
			}

			// lines cursor
			if (enabledCursor) {
				g.setColor(Color.blue);

				{
					int dx = (int) game.getView().getController().getScreenX(mgridx);
					g.drawLine(dx, 0, dx, game.canvas.getHeight());
				}

				{
					int dy = (int) game.getView().getController().getScreenY(mgridy);
					g.drawLine(0, dy, game.canvas.getWidth(), dy);
				}

			}

			// labels
			if (enabledGrid) {
				// X座標ラベルの描画位置カウンタ
				int drawY = 0;

				for (double x : linesX) {
					int dx = (int) game.getView().getController().getScreenX(x);
					String str = HString.getEffectiveExpression(x, effectiveDigitW);

					g.setFont(font);
					drawBoldString(g, str, dx, (1 + drawY) * g.getFont().getSize(), Color.white, Color.black);

					int textLength = g.getFontMetrics().stringWidth(str);
					drawY = (drawY + 1) % (1 + (int) (textLength / gridSpaceDispW));
				}

				for (double y : linesY) {
					int dy = (int) game.getView().getController().getScreenY(y);
					String str = HString.getEffectiveExpression(y, effectiveDigitH);

					g.setFont(font);
					drawBoldString(g, str, 0, dy + g.getFont().getSize(), Color.white, Color.black);
				}
			}

			// labels cursor
			if (enabledCursor) {

				{
					StructureComplex buffer = new StructureComplex(
						mgridx,
						mgridy);
					String[] valueInformation = game.getFunction().getValueInformation(buffer);

					for (int i = 0; i < valueInformation.length; i++) {
						String str = valueInformation[i];

						g.setFont(font);
						drawBoldString(g, str,
							(int) (game.getView().getController().getScreenX(mgridx) + 2),
							(int) (game.getView().getController().getScreenY(mgridy) - 2 - g.getFont().getSize() * ((1 + valueInformation.length) - i)),
							Color.white, Color.black);
					}
				}

				{
					String str = "Re: " + HString.getEffectiveExpression(
						mgridx, effectiveDigitW + 4);

					g.setFont(font);
					drawBoldString(g, str,
						(int) (game.getView().getController().getScreenX(mgridx) + 2),
						(int) (game.getView().getController().getScreenY(mgridy) - 2 - g.getFont().getSize()),
						Color.yellow, Color.black);
				}

				{
					String str = "Im: " + HString.getEffectiveExpression(
						mgridy, effectiveDigitH + 4);

					g.setFont(font);
					drawBoldString(g, str,
						(int) (game.getView().getController().getScreenX(mgridx) + 2),
						(int) (game.getView().getController().getScreenY(mgridy) - 2),
						Color.yellow, Color.black);
				}

			}

		}
	}

	@Override
	public Optional<RectangleCoordinate> getOpticalBounds(Layer layer)
	{
		if (layer == game.layerOverlay) return Optional.of(game.getView().getController().getCoordinateRectangle());
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
