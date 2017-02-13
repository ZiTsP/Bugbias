package libraries.putils;

import java.text.DecimalFormat;
import java.util.stream.IntStream;

public class ReduceLength {

	private ReduceLength() {
	}

	private static final int DEFAULT_DECIMAL_LENGTH = 4;

	public static String reduceDecimal(Double val) {
		return reduceDecimal(val, DEFAULT_DECIMAL_LENGTH);
	}

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##########.##########");

	public static String reduceDecimal(Double val, int length) {
		String str = String.valueOf(DECIMAL_FORMAT.format(val));
		if (!str.contains(".")) {
			StringBuffer sb = new StringBuffer();
			sb.append(str);
			sb.append(".");
			IntStream.rangeClosed(1, length).forEach(e -> sb.append("0"));
			return sb.toString();
		}
		int index = str.indexOf(".");
		if (length < str.length() - index) {
			return str.substring(0, index + length + 1);
		}
		int diff = str.length() - index;
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		for (int i = diff; i <= length; i++) {
			sb.append(0);
		}
		return sb.toString();
	}
}
