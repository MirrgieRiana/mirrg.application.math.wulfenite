package mirrg.application.math.wulfenite.core;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.TextField;
import java.text.DecimalFormat;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mirrg.helium.swing.phosphorus.canvas.game.EventGamePhosphorus;

public class DialogCoordinate extends JDialog
{

	private Wulfenite game;

	private JFormattedTextField textFieldX;
	private JFormattedTextField textFieldY;
	private JFormattedTextField textFieldZoomX;
	private JFormattedTextField textFieldZoomY;

	public DialogCoordinate(Wulfenite game, JFrame frame)
	{
		super(frame, "座標設定");
		this.game = game;

		add(createBorderPanelDown(
			createBorderPanelUp(
				createBorderPanelRight(
					createBorderPanelLeft(
						new JLabel("X"),
						get(() -> {
							textFieldX = new JFormattedTextField(new DecimalFormat("0.00000000"));
							textFieldX.setValue(game.getView().x);
							textFieldX.setColumns(10);
							textFieldX.setAlignmentX(TextField.RIGHT_ALIGNMENT);
							textFieldX.addActionListener(e -> {
								pre();
								updateX();
								post();
							});
							return textFieldX;
						})),
					createButton("設定", e -> {
						pre();
						updateX();
						post();
					})),
				createBorderPanelRight(
					createBorderPanelLeft(
						new JLabel("Y"),
						get(() -> {
							textFieldY = new JFormattedTextField(new DecimalFormat("0.00000000"));
							textFieldY.setValue(game.getView().y);
							textFieldY.setColumns(10);
							textFieldY.setAlignmentX(TextField.RIGHT_ALIGNMENT);
							textFieldY.addActionListener(e -> {
								pre();
								updateY();
								post();
							});
							return textFieldY;
						})),
					createButton("設定", e -> {
						pre();
						updateY();
						post();
					})),
				createBorderPanelRight(
					createBorderPanelLeft(
						new JLabel("X"),
						get(() -> {
							textFieldZoomX = new JFormattedTextField(new DecimalFormat("0.00000000"));
							textFieldZoomX.setValue(game.getView().zoomX);
							textFieldZoomX.setColumns(10);
							textFieldZoomX.setAlignmentX(TextField.RIGHT_ALIGNMENT);
							textFieldZoomX.addActionListener(e -> {
								pre();
								updateZoomX();
								post();
							});
							return textFieldZoomX;
						})),
					createButton("設定", e -> {
						pre();
						updateZoomX();
						post();
					})),
				createBorderPanelRight(
					createBorderPanelLeft(
						new JLabel("X"),
						get(() -> {
							textFieldZoomY = new JFormattedTextField(new DecimalFormat("0.00000000"));
							textFieldZoomY.setValue(game.getView().zoomY);
							textFieldZoomY.setColumns(10);
							textFieldZoomY.setAlignmentX(TextField.RIGHT_ALIGNMENT);
							textFieldZoomY.addActionListener(e -> {
								pre();
								updateZoomY();
								post();
							});
							return textFieldZoomY;
						})),
					createButton("設定", e -> {
						pre();
						updateZoomY();
						post();
					})),
				null),
			createBorderPanelLeft(
				createButton("リセット", e -> {
					setX(1);
					setY(1);
					setZoomX(100);
					setZoomY(100);
					post();
				}),
				null)));
	}

	//

	private void setX(double value)
	{
		textFieldX.setValue(value);
		updateX();
	}

	private void setY(double value)
	{
		textFieldY.setValue(value);
		updateY();
	}

	private void setZoomX(double value)
	{
		textFieldZoomX.setValue(value);
		updateZoomX();
	}

	private void setZoomY(double value)
	{
		textFieldZoomY.setValue(value);
		updateZoomY();
	}

	//

	private void updateX()
	{
		game.getView().x = ((Number) textFieldX.getValue()).doubleValue();
	}

	private void updateY()
	{
		game.getView().y = ((Number) textFieldY.getValue()).doubleValue();
	}

	private void updateZoomX()
	{
		game.getView().zoomX = ((Number) textFieldZoomX.getValue()).doubleValue();
	}

	private void updateZoomY()
	{
		game.getView().zoomY = ((Number) textFieldZoomY.getValue()).doubleValue();
	}

	//

	private void pre()
	{
		game.event().post(new EventGamePhosphorus.ChangeViewStatus.Pre());
	}

	private void post()
	{
		game.event().post(new EventGamePhosphorus.ChangeViewStatus.Post());
	}

}
