package mirrg.application.math.wulfenite.core.dialog;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.Component;
import java.util.function.Consumer;

import javax.swing.JLabel;

public abstract class Parameter<T, C extends Component>
{

	protected String name;
	protected Consumer<T> setter;
	protected Runnable pre;
	protected Runnable post;

	protected C component;

	public Parameter(String name, Consumer<T> setter, Runnable pre, Runnable post)
	{
		this.name = name;
		this.setter = setter;
		this.pre = pre;
		this.post = post;
	}

	public Component initRecord()
	{
		return createBorderPanelRight(
			createBorderPanelLeft(
				new JLabel(name),
				initTextField()),
			createButton("設定", e -> {
				pre.run();
				update();
				post.run();
			}));
	}

	public C initTextField()
	{
		component = createComponent();
		return component;
	}

	protected abstract C createComponent();

	/**
	 * コンポーネントの値を変更し、セッターにも出力する。
	 */
	public void set(T value)
	{
		setToComponent(value);
		update();
	}

	/**
	 * コンポーネントの値をセッターに出力する。
	 */
	public void update()
	{
		setter.accept(getFromComponent());
	}

	protected abstract void setToComponent(T t);

	protected abstract T getFromComponent();

}
