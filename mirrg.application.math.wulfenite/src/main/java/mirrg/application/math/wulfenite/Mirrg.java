package mirrg.application.math.wulfenite;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

// TODO mirrg
public class Mirrg
{

	public static String encodeRGB(Color color)
	{
		return String.format("#%02x%02x%02x",
			color.getRed(),
			color.getGreen(),
			color.getBlue());
	}

	public static String toString(Exception e)
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
