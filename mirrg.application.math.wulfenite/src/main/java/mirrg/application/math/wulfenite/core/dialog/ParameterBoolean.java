package mirrg.application.math.wulfenite.core.dialog;

import java.awt.Component;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JCheckBox;

public class ParameterBoolean extends ParameterBase<Boolean, JCheckBox>
{

	public ParameterBoolean(String name, Supplier<Boolean> getter, Consumer<Boolean> setter, Runnable pre, Runnable post)
	{
		super(name, getter, setter, pre, post);
	}

	@Override
	public Component initRecord()
	{
		return initComponent();
	}

	@Override
	protected JCheckBox createComponent()
	{
		JCheckBox component = new JCheckBox(name);
		component.setSelected(getter.get());
		component.addActionListener(e -> {
			pre.run();
			update();
			post.run();
		});
		return component;
	}

	@Override
	protected void setToComponent(Boolean t)
	{
		component.setSelected(t);
	}

	@Override
	protected Boolean getFromComponent()
	{
		return component.isSelected();
	}

}
