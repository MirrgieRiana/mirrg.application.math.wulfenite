package mirrg.application.math.wulfenite.core.dialog;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.TextField;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import mirrg.helium.standard.hydrogen.util.HString;

public class ParameterColor extends ParameterBase<Color, JTextField>
{

	public ParameterColor(String name, Supplier<Color> getter, Consumer<Color> setter, Runnable pre, Runnable post)
	{
		super(name, getter, setter, pre, post);
	}

	@Override
	public Component initRecord()
	{
		JColorChooser colorChooser = new JColorChooser();
		return createBorderPanelRight(
			createBorderPanelLeft(
				new JLabel(name),
				initComponent()),
			createButton("...", e -> {
				Color color = JColorChooser.showDialog(colorChooser, name, getter.get());
				if (color != null) {
					pre.run();
					set(color);
					post.run();
				}
			}),
			createButton("設定", e -> {
				pre.run();
				update();
				post.run();
			}));
	}

	@Override
	protected JTextField createComponent()
	{
		JTextField component = new JTextField();
		component.setText(format(getter.get()));
		component.setColumns(10);
		component.setAlignmentX(TextField.RIGHT_ALIGNMENT);
		component.addActionListener(e -> {
			pre.run();
			update();
			post.run();
		});
		return component;
	}

	private String format(Color color)
	{
		String str = Integer.toString(color.getRGB() & 0xffffff, 16).toUpperCase();
		str = HString.rept("0", 6 - str.length()) + str;
		return str;
	}

	@Override
	protected void setToComponent(Color t)
	{
		component.setText(format(t));
	}

	@Override
	protected Color getFromComponent()
	{
		Color color;
		try {
			color = Color.decode("#" + component.getText());
		} catch (NumberFormatException e) {
			return Color.black;
		}
		return color;
	}

}
