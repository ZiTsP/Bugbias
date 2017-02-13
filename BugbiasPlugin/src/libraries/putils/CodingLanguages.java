package libraries.putils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CodingLanguages {

    private CodingLanguages() {
    }

    public static final CodingLanguage EMPTY = new CodingLanguage("(Empty)", Arrays.asList(""), null);
    public static final CodingLanguage JAVA = new CodingLanguage("Java", Arrays.asList(".java"), "//");
    public static final CodingLanguage SCALA = new CodingLanguage("Scala", Arrays.asList(".scala", ".java"), "//");
    public static final CodingLanguage C = new CodingLanguage("C", Arrays.asList(".c"), "//");
    public static final CodingLanguage PERL = new CodingLanguage("Perl", Arrays.asList(".pl", ".cgi"), "#");
    public static final CodingLanguage PYTHON = new CodingLanguage("Python", Arrays.asList(".py"), "#");
//      tmpExtention = new ArrayList<>();
//      tmpExtention.add(".bar1");
//      tmpExtention.add(".bar2");
//      BAR = new CodingLanguage("Bar", tmpExtention);

    private static final List<CodingLanguage> languageList = new ArrayList<>();
    static {
        languageList.add(JAVA);
        languageList.add(SCALA);
        languageList.add(PERL);
        languageList.add(PYTHON);
    }

    public static final List<CodingLanguage> getAll() {
        return languageList;
    }

    public static Optional<CodingLanguage> getLanguage(String extension) {
        if (extension == null) {
            throw new NullPointerException();
        }
        return languageList.stream().filter(e -> e.getExtension().contains(extension)).findAny();
    }

    public static List<String> getExtension(String language) {
        for (CodingLanguage entry : languageList) {
            if (entry.getLanguage().equals(language)) {
                return entry.getExtension();
            }
        }
        return new ArrayList<>();
    }

    public static Optional<CodingLanguage> parse(String language) {
        if (language == null) {
            throw new NullPointerException();
        }
        return languageList.stream().filter(e -> e.getLanguage().toLowerCase().equals(language.toLowerCase())).findFirst();
    }

    public static boolean has(String language) {
        if (language != null && parse(language).isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean has(CodingLanguage language) {
        if (language != null && languageList.contains(language)) {
            return true;
        } else {
            return false;
        }
    }

    public static List<String> getExtensions() {
        List<String> extensions = new ArrayList<>();
        getAll().stream().forEach(lang -> extensions.addAll(lang.getExtension()));
        return extensions;
    }
}
