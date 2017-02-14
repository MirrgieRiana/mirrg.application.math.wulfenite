package mirrg.application.math.wulfenite.core.dialog;

import java.awt.TextField;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JTextField;

public class ParameterString extends ParameterTextFieldBase<String, JTextField>
{

	public ParameterString(String name, Supplier<String> getter, Consumer<String> setter, Runnable pre, Runnable post)
	{
		super(name, getter, setter, pre, post);
	}

	@Override
	protected JTextField createComponent()
	{
		JTextField component = new JTextField();
		component.setText(getter.get());
		component.setColumns(5);
		component.setAlignmentX(TextField.RIGHT_ALIGNMENT);
		component.addActionListener(e -> {
			pre.run();
			update();
			post.run();
		});
		return component;
	}

	@Override
	protected void setToComponent(String t)
	{
		component.setText(t);
	}

	@Override
	protected String getFromComponent()
	{
		return component.getText();
	}

}
