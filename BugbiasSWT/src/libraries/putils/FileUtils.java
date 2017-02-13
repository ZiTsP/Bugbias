package libraries.putils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.IntStream;

public class FileUtils {

    public static Optional<String> getExtension(Path path) {
        String name = path.getFileName().toString();
        int index = name.lastIndexOf(".");
        return (0 < index) ? Optional.of(name.substring(index, name.length())) : Optional.empty();
    }

    public static ArrayList<Path> getNodeFiles(Path path) {
        ArrayList<Path> list = new ArrayList<>();
        try {
            Files.list(path).filter(e -> !e.startsWith(".")).forEach(e ->{
                if (Files.isDirectory(e)) {
                    list.addAll(getNodeFiles(e));
                }
                getExtension(e).ifPresent(f -> list.add(e));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Optional<Path> rename(Path oldPath, String prefix, String suffix) {
        if (oldPath == null || Files.exists(oldPath)) {
            return Optional.empty();
        }
        String parent = oldPath.getParent().toAbsolutePath().toString();
        String name, extension;
        String fileName[] = oldPath.getFileName().toString().split(".");
        if (fileName.length >= 2) {
            extension = fileName[fileName.length -1];
            StringJoiner str = new StringJoiner(".");
            IntStream.range(0, fileName.length - 1).forEach(i -> str.add(fileName[i]));
            name = str.toString();
        } else {
            name = fileName[0];
            extension = "";
        }
        StringBuffer newName = new StringBuffer();
        Optional<String> preStr = Optional.ofNullable(prefix);
        Optional<String> sufStr = Optional.ofNullable(suffix);
        preStr.ifPresent(str -> newName.append(str));
        newName.append(name);
        sufStr.ifPresent(str -> newName.append(str));
        newName.append(extension);
        return Optional.of(Paths.get(parent, newName.toString()));
    }

    public static String parsePath(String headPath, String tailPath) {
        String head[] = headPath.split("/");
        String tail[] = Arrays.asList(tailPath.split("/")).stream().filter(e -> e != null && !e.equals("")).toArray(String[]::new);
        int matchIndex[] = IntStream.range(0, head.length).filter(i -> head[i].equals(tail[0])).toArray();
        if (matchIndex == null || matchIndex.length <= 0) {
            return (new StringBuffer(headPath)).append(tailPath).toString();
        }
        for (int index : matchIndex) {
            if (IntStream.range(index, head.length).allMatch(i -> head[i].equals(tail[i - index])) == true) {
                StringJoiner newStr = new StringJoiner("/");
                IntStream.range(0, index).forEachOrdered(i -> newStr.add(head[i]));
                IntStream.range(0, tail.length).forEachOrdered(i -> newStr.add(tail[i]));
                return newStr.toString();
            }
        }
        return (new StringBuffer(headPath)).append(tailPath).toString();
    }
}
