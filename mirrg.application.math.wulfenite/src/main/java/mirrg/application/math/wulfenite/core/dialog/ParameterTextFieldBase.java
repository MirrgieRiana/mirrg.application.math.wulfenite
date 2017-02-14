package mirrg.application.math.wulfenite.core.dialog;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.Component;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JLabel;

public abstract class ParameterTextFieldBase<T, C extends Component> extends ParameterBase<T, C>
{

	public ParameterTextFieldBase(String name, Supplier<T> getter, Consumer<T> setter, Runnable pre, Runnable post)
	{
		super(name, getter, setter, pre, post);
	}

	@Override
	public Component initRecord()
	{
		return createBorderPanelRight(
			createBorderPanelLeft(
				new JLabel(name),
				initComponent()),
			createButton("設定", e -> {
				pre.run();
				update();
				post.run();
			}));
	}

}
