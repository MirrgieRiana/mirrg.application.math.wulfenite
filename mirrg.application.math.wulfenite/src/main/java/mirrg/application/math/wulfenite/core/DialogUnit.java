package mirrg.application.math.wulfenite.core;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.TextField;
import java.text.DecimalFormat;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import mirrg.helium.swing.phosphorus.canvas.game.render.Layer;

public class DialogUnit extends JDialog
{

	private Wulfenite game;

	private JFormattedTextField textFieldX;
	private JFormattedTextField textFieldY;
	private JTextField textFieldNameX;
	private JTextField textFieldNameY;

	public DialogUnit(Wulfenite game, JFrame frame)
	{
		super(frame, "単位設定");
		this.game = game;

		add(createBorderPanelDown(
			createBorderPanelUp(
				createBorderPanelRight(
					createBorderPanelLeft(
						new JLabel("実軸"),
						get(() -> {
							textFieldX = new JFormattedTextField(new DecimalFormat("0.00000000"));
							textFieldX.setValue(game.getModel().grid.unitX);
							textFieldX.setColumns(10);
							textFieldX.setAlignmentX(TextField.RIGHT_ALIGNMENT);
							textFieldX.addActionListener(e -> {
								updateX();
								dirty();
							});
							return textFieldX;
						})),
					get(() -> {
						textFieldNameX = new JTextField();
						textFieldNameX.setText(game.getModel().grid.unitNameX);
						textFieldNameX.setColumns(5);
						textFieldNameX.setAlignmentX(TextField.RIGHT_ALIGNMENT);
						textFieldNameX.addActionListener(e -> {
							updateNameX();
							dirty();
						});
						return textFieldNameX;
					}),
					createButton("設定", e -> {
						updateX();
						updateNameX();
						dirty();
					})),
				createBorderPanelRight(
					createBorderPanelLeft(
						new JLabel("虚軸"),
						get(() -> {
							textFieldY = new JFormattedTextField(new DecimalFormat("0.00000000"));
							textFieldY.setValue(game.getModel().grid.unitY);
							textFieldY.setColumns(10);
							textFieldY.setAlignmentX(TextField.RIGHT_ALIGNMENT);
							textFieldY.addActionListener(e -> {
								updateY();
								dirty();
							});
							return textFieldY;
						})),
					get(() -> {
						textFieldNameY = new JTextField();
						textFieldNameY.setText(game.getModel().grid.unitNameY);
						textFieldNameY.setColumns(5);
						textFieldNameY.setAlignmentX(TextField.RIGHT_ALIGNMENT);
						textFieldNameY.addActionListener(e -> {
							updateNameY();
							dirty();
						});
						return textFieldNameY;
					}),
					createButton("設定", e -> {
						updateY();
						updateNameY();
						dirty();
					})),
				null),
			createBorderPanelLeft(
				createButton("リセット", e -> {
					setX(1);
					setY(1);
					setNameX("");
					setNameY("");
					dirty();
				}), createButton("π", e -> {
					setX(Math.PI);
					setY(Math.PI);
					setNameX("π");
					setNameY("π");
					dirty();
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

	private void setNameX(String value)
	{
		textFieldNameX.setText(value);
		updateNameX();
	}

	private void setNameY(String value)
	{
		textFieldNameY.setText(value);
		updateNameY();
	}

	//

	private void updateX()
	{
		game.getModel().grid.unitX = ((Number) textFieldX.getValue()).doubleValue();
	}

	private void updateY()
	{
		game.getModel().grid.unitY = ((Number) textFieldY.getValue()).doubleValue();
	}

	private void updateNameX()
	{
		game.getModel().grid.unitNameX = textFieldNameX.getText();
	}

	private void updateNameY()
	{
		game.getModel().grid.unitNameY = textFieldNameY.getText();
	}

	//

	private void dirty()
	{
		game.getLayers().forEach(Layer::dirty);
	}

}
