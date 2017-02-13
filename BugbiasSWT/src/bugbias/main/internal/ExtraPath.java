package bugbias.main.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtraPath {
        
    private ExtraPath() {
    }

    public static final Path ROOT_PATH;
    static {
        ExtraPath tmpClass = new ExtraPath();
        URL url = tmpClass.getClass().getClassLoader().getResource("");
        URI uri = null;
        try {
            uri = new URI(url.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ROOT_PATH = (uri != null) ? Paths.get(uri) : Paths.get("");
    }
}
