package bugbias.main.internal;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import libraries.putils.FileUtils;


public class Extensions {

    public static List<Path> extractSpecificType(List<Path> list, List<String> extensions) {
        return list.stream()
            .filter(e -> extensions.contains(FileUtils.getExtension(e).orElse("")))
            .collect(Collectors.toList());
    }
}
