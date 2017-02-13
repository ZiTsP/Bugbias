package libraries.revisiontrace.svn;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.IntStream;

public class SvnPaths {
    
    private SvnPaths() {
    }

    public static String attach(String rootUrl, String svnFilePath) {
        String head[] = rootUrl.split("/");
        String tail[] = Arrays.asList(svnFilePath.split("/")).stream().filter(e -> e != null && !e.equals("")).toArray(String[]::new);
        int matchIndex[] = IntStream.range(0, head.length).filter(i -> head[i].equals(tail[0])).toArray();
        if (matchIndex != null && 0 < matchIndex.length) {
            for (int index : matchIndex) {
                if (IntStream.range(index, head.length).allMatch(i -> head[i].equals(tail[i - index])) == true) {
                    StringJoiner newStr = new StringJoiner("/");
                    IntStream.range(0, index).forEachOrdered(i -> newStr.add(head[i]));
                    IntStream.range(0, tail.length).forEachOrdered(i -> newStr.add(tail[i]));
                    return newStr.toString();
                }
            }
        }
        StringJoiner newStr = new StringJoiner("/");
        IntStream.range(0, head.length).forEachOrdered(i -> newStr.add(head[i]));
        IntStream.range(0, tail.length).forEachOrdered(i -> newStr.add(tail[i]));
        return newStr.toString();
    }
    
    public static String detach(String rootUrl, String svnFilePath) {
        String absoluteUrl = attach(rootUrl, svnFilePath);
        return absoluteUrl.substring(rootUrl.length() + (rootUrl.endsWith("/") ? 0 : 1));
    }
    
    public static void main(String[] args) {
        String aPath = "/hoge/hoge/foo/";
        String bPath = "/foo/bar/bar";
        String cPath = "foge/foge/";
        System.out.println(attach(aPath, bPath));
        System.out.println(detach(aPath, bPath));
        System.out.println(attach(cPath, detach(aPath, bPath)));
        
    }
}
