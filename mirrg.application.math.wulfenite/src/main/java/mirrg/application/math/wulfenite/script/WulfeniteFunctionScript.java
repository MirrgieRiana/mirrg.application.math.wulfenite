package mirrg.application.math.wulfenite.script;

import java.util.Optional;
import java.util.function.Consumer;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import mirrg.application.math.wulfenite.core.Wulfenite;
import mirrg.application.math.wulfenite.core.WulfeniteFunctionBase;
import mirrg.application.math.wulfenite.core.WulfeniteFunctionComplex;
import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.compile.oxygen.parser.core.ResultOxygen;
import mirrg.helium.math.hydrogen.complex.StructureComplex;

public class WulfeniteFunctionScript extends WulfeniteFunctionBase
{

	public WulfeniteFunctionScript(Wulfenite wulfenite)
	{
		super(wulfenite);
	}

	@Override
	public int getColor(StructureComplex coordinate)
	{
		getValue(coordinate);
		return 0xff000000 | WulfeniteFunctionComplex.getColorIntFromComplex(coordinate);
	}

	@Override
	public boolean isValuePresent()
	{
		return true;
	}

	private String source = "";

	@XStreamOmitField
	private Optional<Consumer<StructureComplex>> consumer;

	private void onChangeSource()
	{
		ResultOxygen<Consumer<StructureComplex>> result = WulfeniteScript.getSyntax().matches(source);
		wulfenite.fireChangeFunction(() -> {
			consumer = result.isValid ? Optional.of(result.node.value) : Optional.empty();
		});
	}

	@Override
	public void getValue(StructureComplex buffer)
	{
		if (consumer == null) onChangeSource();
		if (consumer.isPresent()) consumer.get().accept(buffer);
	}

	@XStreamOmitField
	private DialogWulfeniteScript dialog;

	@Override
	public void toggleDialog()
	{
		if (dialog == null) {
			dialog = new DialogWulfeniteScript(source);
			dialog.textPaneOxygen.event().register(EventTextPaneOxygen.ChangeSource.class, e -> {
				source = dialog.textPaneOxygen.getText();
			});
			dialog.textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Success.class, e -> {
				onChangeSource();
			});
		}
		dialog.setVisible(!dialog.isVisible());
	}

	@Override
	public void dispose()
	{
		if (dialog != null) dialog.dispose();
	}

}
