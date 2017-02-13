package libraries.revisiontrace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import libraries.putils.Charsets;

public class LinesExtraction {

    private static final String ADDED_LINE = "+";
    private static final String REMOVED_LINE = "-";
    private static final String FROMPATH_LINE = "---";
    private static final String TOPATH_LINE = "+++";

    public static boolean extractAddedLine(InputStream input, OutputStream output) {
        return extractPrefixedLine(input, output, ADDED_LINE);
    }
    
    public static boolean extractRemovedLine(InputStream input, OutputStream output) {
        return extractPrefixedLine(input, output, REMOVED_LINE);
    }
    
    public static boolean extractPrefixedLine(InputStream input, OutputStream output, String prefix) {
        if (input != null || output != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charsets.UTF8));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, Charsets.UTF8))) {
                while (reader.ready()) {
                    String str = reader.readLine();
                    if (str == null) {
                        break;
                    } else if (!str.startsWith(FROMPATH_LINE) && !str.startsWith(TOPATH_LINE) && str.startsWith(prefix)) {
                        writer.write(str.substring(prefix.length()));
                        writer.newLine();
                    } else {
                        writer.newLine();
                    }
                }
                writer.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public static ArrayList<String> extractPrefixedLine(Path filePath, String prefix) {
        ArrayList<String> newLines = new ArrayList<>();
        if (filePath != null && Files.exists(filePath)) {
            try {
                Files.lines(filePath, Charsets.getDefault()).forEachOrdered(line -> {
                    if (!line.startsWith(FROMPATH_LINE) && !line.startsWith(TOPATH_LINE) && line.startsWith(prefix)) {
                        newLines.add(line.substring(prefix.length()));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newLines;
    }
    

    public static ArrayList<String> extractAddedLine(List<String> lines) {
        return extractPrefixedLine(lines, ADDED_LINE);
    }
    
    public static ArrayList<String> extractRemovedLine(List<String> lines) {
        return extractPrefixedLine(lines, REMOVED_LINE);
    }
    
    public static ArrayList<String> extractPrefixedLine(List<String> lines, String prefix) {
        ArrayList<String> newLines = new ArrayList<>();
        lines.stream().forEachOrdered(line -> {
            if (!line.startsWith(FROMPATH_LINE) && !line.startsWith(TOPATH_LINE) && line.startsWith(prefix)) {
                newLines.add(line.substring(prefix.length()));
            }
        });
        return newLines;
    }
}
