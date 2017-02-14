package mirrg.application.math.wulfenite.core.dialog;

import java.awt.Component;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ParameterBase<T, C extends Component>
{

	protected String name;
	protected Supplier<T> getter;
	protected Consumer<T> setter;
	protected Runnable pre;
	protected Runnable post;

	protected C component;

	public ParameterBase(String name, Supplier<T> getter, Consumer<T> setter, Runnable pre, Runnable post)
	{
		this.name = name;
		this.getter = getter;
		this.setter = setter;
		this.pre = pre;
		this.post = post;
	}

	public abstract Component initRecord();

	public C initComponent()
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
