package libraries.putils;

public class PlatformInfo{
	private static final String PLATFORM_NAME = System.getProperty("os.name").toLowerCase();
	private static final String PLATFORM_ARCHITECTURE = System.getProperty("os.arch");
	
	public static final boolean isLinux() {
		return PLATFORM_NAME.startsWith("linux");
	}
	
	public static final boolean isMac() {
		return PLATFORM_NAME.startsWith("mac");
	}
	
	public static final boolean isWindows() {
		return PLATFORM_NAME.startsWith("windows");
	}
	
	public static final boolean isUnix() {
	    return (isLinux() == true || isMac() == true) ? true : false;
	}

	public enum PLATFORM {WINDOWS32, WINDOWS64, LINUX, MAC, OTHERS};

	public static final PLATFORM getPlatform() {
		if (isLinux() == true) {
			return PLATFORM.LINUX;
		} else if (isMac() == true) {
			return PLATFORM.MAC;
		} else if (isWindows() == true && PlatformInfo.is32bitArch()) {
	    	return PLATFORM.WINDOWS32;
		} else if (isWindows() == true && PlatformInfo.is64bitArch()) {
	    	return PLATFORM.WINDOWS64;
		} else{
			return PLATFORM.OTHERS;
		}
	}
	
	public static final String getName() {
		if (isLinux() == true) {
			return ("linux");
		} else if (isMac() == true) {
			return ("mac");
		} else if (isWindows() == true) {
			return ("windows");
		} else{
			return ("other");
		}
	}
	
	public static final boolean is32bitArch() {
		return PLATFORM_ARCHITECTURE.endsWith("86");
	}
	
	public static final boolean is64bitArch() {
		return PLATFORM_ARCHITECTURE.endsWith("64");
	}
	
	public static final byte getPlatformArch() {
		if (is32bitArch() == true) {
			return (32);
		} else if (is64bitArch() == true) {
			return (64);
		} else{
			return (-1);
		}
	}
}