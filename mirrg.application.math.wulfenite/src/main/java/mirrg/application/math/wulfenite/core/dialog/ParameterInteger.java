package mirrg.application.math.wulfenite.core.dialog;

import java.awt.TextField;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JFormattedTextField;

public class ParameterInteger extends ParameterTextFieldBase<Integer, JFormattedTextField>
{

	public ParameterInteger(String name, Supplier<Integer> getter, Consumer<Integer> setter, Runnable pre, Runnable post)
	{
		super(name, getter, setter, pre, post);
	}

	@Override
	protected JFormattedTextField createComponent()
	{
		JFormattedTextField component = new JFormattedTextField(new DecimalFormat("0"));
		component.setValue(getter.get());
		component.setColumns(10);
		component.setAlignmentX(TextField.RIGHT_ALIGNMENT);
		component.addActionListener(e -> {
			pre.run();
			update();
			post.run();
		});
		return component;
	}

	@Override
	protected void setToComponent(Integer t)
	{
		component.setValue(t);
	}

	@Override
	protected Integer getFromComponent()
	{
		return ((Number) component.getValue()).intValue();
	}

}
