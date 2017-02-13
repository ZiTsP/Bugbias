package libraries.putils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Charsets {
	
	private Charsets() {
	}

	public static final Charset WINDOWS_SJIS = Charset.forName("MS932");
	public static final Charset UTF8 = StandardCharsets.UTF_8;
	public static final Charset ASCII = StandardCharsets.US_ASCII;

	public static Charset getDefault() {
		if (PlatformInfo.isWindows()) {
			return WINDOWS_SJIS;
		} else if (PlatformInfo.isUnix()) {
			return UTF8;
		} else {
			return ASCII;
		}
	}
}
