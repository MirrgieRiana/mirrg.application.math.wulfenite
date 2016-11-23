package mirrg.application.math.wulfenite.script;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import mirrg.application.math.wulfenite.Mirrg;
import mirrg.application.math.wulfenite.script.core.WulfeniteScript;
import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.standard.hydrogen.util.HString.LineProvider;

public class DialogWulfeniteScript extends JDialog
{

	public TextPaneOxygenWulfeniteScript textPaneOxygen;
	public JTextPane textPaneOut;

	public DialogWulfeniteScript(Frame parent, String source)
	{
		super(parent, "Wulfenite Script");

		add(process(createSplitPaneVertical(
			createScrollPane(get(() -> {
				textPaneOxygen = new TextPaneOxygenWulfeniteScript(WulfeniteScript.getSyntax());
				textPaneOxygen.setText(source);
				textPaneOxygen.setPreferredSize(new Dimension(500, 100));
				textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Success.class, e -> {
					if (e.timing == EventTextPaneOxygen.Syntax.TIMING_MAIN) {
						if (textPaneOut == null) return;
						textPaneOut.setText("Compiled Successfully");
						textPaneOut.setBackground(Color.decode("#bbffbb"));
					}
				});
				textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Failure.class, e -> {
					if (textPaneOut == null) return;

					LineProvider lineProvider = HString.getLineProvider(textPaneOxygen.getText());
					int index = textPaneOxygen.getResult().getTokenProposalIndex();
					textPaneOut.setText(String.format("[SyntaxError %s] expected: %s\n%s",
						toPosition(lineProvider, index),
						textPaneOxygen.getResult().getTokenProposal().stream()
							.map(p -> p.getName())
							.distinct()
							.collect(Collectors.joining(" ")),
						String.join("\n", getPositionString(lineProvider, index))));
					textPaneOut.setBackground(Color.decode("#ffbbbb"));
				});
				textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Error.class, e -> {
					if (textPaneOut == null) return;
					textPaneOut.setText("" + Mirrg.toString(e.exception));
					textPaneOut.setBackground(Color.decode("#ffbbbb"));
				});
				return textPaneOxygen;
			})),
			createScrollPane(get(() -> {
				textPaneOut = new JTextPane();
				textPaneOut.setEditable(false);
				textPaneOut.setFont(new Font(Font.MONOSPACED, textPaneOut.getFont().getStyle(), textPaneOut.getFont().getSize()));
				return textPaneOut;
			}))),
			c -> {
				// TODO mirrg
				((JSplitPane) c).setResizeWeight(1);
			}));

		pack();
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setLocationByPlatform(true);
	}

	public static String toPosition(String source, int characterIndex)
	{
		return toPosition(HString.getLineProvider(source), characterIndex);
	}

	public static String toPosition(LineProvider lineProvider, int characterIndex)
	{
		int row = lineProvider.getLineNumber(characterIndex);
		int column = characterIndex - lineProvider.getStartIndex(row);

		return String.format("R:%d, C:%d", row, column);
	}

	public static String[] getPositionString(LineProvider lineProvider, int characterIndex)
	{
		int row = lineProvider.getLineNumber(characterIndex);
		int column = characterIndex - lineProvider.getStartIndex(row);
		String line = lineProvider.getContent(row);

		return new String[] {
			line,
			HString.rept(" ", column) + "^",
		};
	}

}
