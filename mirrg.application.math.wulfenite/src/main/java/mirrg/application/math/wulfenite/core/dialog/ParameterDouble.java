package mirrg.application.math.wulfenite.core.dialog;

import java.awt.TextField;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JFormattedTextField;

public class ParameterDouble extends Parameter<Double, JFormattedTextField>
{

	public ParameterDouble(String name, Supplier<Double> getter, Consumer<Double> setter, Runnable pre, Runnable post)
	{
		super(name, getter, setter, pre, post);
	}

	@Override
	protected JFormattedTextField createComponent()
	{
		JFormattedTextField component = new JFormattedTextField(new DecimalFormat("0.00000000"));
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
	protected void setToComponent(Double t)
	{
		component.setValue(t);
	}

	@Override
	protected Double getFromComponent()
	{
		return ((Number) component.getValue()).doubleValue();
	}

}
