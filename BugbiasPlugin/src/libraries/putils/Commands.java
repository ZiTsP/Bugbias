package libraries.putils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class Commands {

	private Commands() {
	}

	public static final <T extends File> ArrayList<String> cat(T file) {
		return cat(file.toPath());
	}
	
	public static final ArrayList<String> cat(Path path) {
		ArrayList<String> list = new ArrayList<>();
    	if (PlatformInfo.isWindows() == true) {
    		list.add("cmd");
    		list.add("/c");
    		list.add("type");
    		list.add(path.toAbsolutePath().toString());
    	} else if (PlatformInfo.isUnix() == true) {
    		list.add("cat");
    		list.add(path.toAbsolutePath().toString());
    	}
		return list;
	}
}
