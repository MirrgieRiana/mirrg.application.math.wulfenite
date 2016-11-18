package mirrg.application.math.wulfenite.script;

import static mirrg.helium.swing.nitrogen.util.HSwing.*;

import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import mirrg.helium.compile.oxygen.editor.EventTextPaneOxygen;
import mirrg.helium.compile.oxygen.editor.TextPaneOxygen;
import mirrg.helium.standard.hydrogen.util.HString;
import mirrg.helium.standard.hydrogen.util.HString.LineProvider;

public class DialogWulfeniteScript extends JDialog
{

	public TextPaneOxygen<?> textPaneOxygen;
	public JTextPane textPaneOut;

	public DialogWulfeniteScript(String source)
	{
		add(createSplitPaneVertical(
			createScrollPane(get(() -> {
				textPaneOxygen = new TextPaneOxygen<>(WulfeniteScript.getSyntax());
				textPaneOxygen.setText(source);
				textPaneOxygen.setPreferredSize(new Dimension(500, 100));
				textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Success.Main.class, e -> {
					if (textPaneOut == null) return;
					textPaneOut.setText("" + textPaneOxygen.getValue());
				});
				textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Failure.class, e -> {
					if (textPaneOut == null) return;

					LineProvider lineProvider = HString.getLineProvider(textPaneOxygen.getText());
					int index = textPaneOxygen.getResult().getTokenProposalIndex();
					textPaneOut.setText(String.format("[SyntaxError%s] expected: %s\n%s",
						toPosition(lineProvider, index),
						textPaneOxygen.getResult().getTokenProposal().stream()
							.map(p -> p.getName())
							.distinct()
							.collect(Collectors.joining(" ")),
						String.join("\n", getPositionString(lineProvider, index))));
				});
				textPaneOxygen.event().register(EventTextPaneOxygen.Syntax.Error.class, e -> {
					if (textPaneOut == null) return;
					textPaneOut.setText("" + toString(e.exception));
				});
				return textPaneOxygen;
			})),
			createScrollPane(get(() -> {
				textPaneOut = new JTextPane();
				textPaneOut.setEditable(false);
				textPaneOut.setFont(new Font(Font.MONOSPACED, textPaneOut.getFont().getStyle(), textPaneOut.getFont().getSize()));
				return textPaneOut;
			}))));

		textPaneOxygen.update();

		pack();
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setLocationByPlatform(true);
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

	// TODO mirrg
	private static String toString(Exception e)
	{
		ArrayList<Byte> bytes = new ArrayList<>();
		try {
			e.printStackTrace(new PrintStream(new OutputStream() {

				@Override
				public void write(int b) throws IOException
				{
					bytes.add((byte) b);
				}

			}, true, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		byte[] bytes2 = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			bytes2[i] = bytes.get(i);
		}
		String string = new String(bytes2);
		return string;
	}

}
