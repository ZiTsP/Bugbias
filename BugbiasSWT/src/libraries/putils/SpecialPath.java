package libraries.putils;

public class SpecialPath {
	
	public static final String  NULL_DEVICE = initNullDevice();

	private static final String initNullDevice() {
    	if (PlatformInfo.isWindows() == true) {
   			return "NUL";
    	} else if (PlatformInfo.isUnix() == true) {
			return "/dev/null";
    	}
		return "";
	}
}