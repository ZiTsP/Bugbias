package libraries.revisiontrace.svn;

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
import java.util.Arrays;
import java.util.List;

import libraries.putils.Charsets;

public final class DiffExtraProcess {
    
    private DiffExtraProcess() {
    }

    private static final String ADDED_LINE = "+";
    private static final String REMOVED_LINE = "-";
    private static final String FROMPATH_LINE = "---";
    private static final String TOPATH_LINE = "+++";

    protected static boolean extractAddedSrcLine(InputStream input, OutputStream output) {
        return extractPrefixedSrcLine(input, output, ADDED_LINE);
    }
    
    protected static boolean extractRemovedSrcLine(InputStream input, OutputStream output) {
        return extractPrefixedSrcLine(input, output, REMOVED_LINE);
    }
    
    protected static boolean extractPrefixedSrcLine(InputStream input, OutputStream output, String prefix) {
        if (input != null || output != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, Charsets.UTF8));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, Charsets.UTF8))) {
                boolean isMimeInfo = false;
                boolean isIgnoreInfo = false;
                int count = 0;
                while (reader.ready()) {
                    String str = reader.readLine();
                    if (str == null) {
                        break;
                    } else if (isMimeInfo == true) {
                        int i = getSvnAttributeInfoLinesCount(str, '-');
                        count += (0 < i) ? i + 1 : 0;
                        i = getSvnAttributeInfoLinesCount(str, '+');
                        count += (0 < i) ? i + 1 : 0;
                        isMimeInfo = false;
                    } else if (isIgnoreInfo == true) {
                        count += getSvnAttributeInfoLinesCount(str, '-');
                        count += getSvnAttributeInfoLinesCount(str, '+');
                        isIgnoreInfo = false;
                    } else if (0 < count) {
                        count -= 1;
                    } else if (str.endsWith(SVN_MIME_TYPE)) {
                        isMimeInfo = true;
                    } else if (str.endsWith(SVN_IGNRE)) {
                        isIgnoreInfo = true;
                    }else if (!str.startsWith(FROMPATH_LINE) && !str.startsWith(TOPATH_LINE) && str.startsWith(prefix)) {
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
    
    protected static ArrayList<String> extractPrefixedSrcLine(Path filePath, String prefix) {
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
        return removeMimeAndIgnoreLines(newLines);
    }

    private static final String SVN_MIME_TYPE = "svn:mime-type";
    private static final String SVN_IGNRE = "svn:ignore";
    
    protected static ArrayList<String> removeMimeAndIgnoreLines(List<String> lines) {
        ArrayList<String> newLines = new ArrayList<>();
        boolean isMimeInfo = false;
        boolean isIgnoreInfo = false;
        int count = 0;
        for (String line : lines) {
            if (isMimeInfo == true) {
                int i = getSvnAttributeInfoLinesCount(line, '-');
                count += (0 < i) ? i + 1 : 0;
                i = getSvnAttributeInfoLinesCount(line, '+');
                count += (0 < i) ? i + 1 : 0;
                isMimeInfo = false;
            } else if (isIgnoreInfo == true) {
                count += getSvnAttributeInfoLinesCount(line, '-');
                count += getSvnAttributeInfoLinesCount(line, '+');
                isIgnoreInfo = false;
            } else if (0 < count) {
                count -= 1;
            } else if (line.endsWith(SVN_MIME_TYPE)) {
                isMimeInfo = true;
            } else if (line.endsWith(SVN_IGNRE)) {
                isIgnoreInfo = true;
            } else {
                newLines.add(line);
            }
        }
        return newLines;
    }
    
    protected static ArrayList<String> removeMimeTypeLines(List<String> lines) {
        ArrayList<String> newLines = new ArrayList<>();
        boolean isMimeInfo = false;
        int count = 0;
        for (String line : lines) {
            if (isMimeInfo == true) {
                int i = getSvnAttributeInfoLinesCount(line, '-');
                count += (0 < i) ? i + 1 : 0;
                i = getSvnAttributeInfoLinesCount(line, '+');
                count += (0 < i) ? i + 1 : 0;
                isMimeInfo = false;
            } else if (0 < count) {
                count -= 1;
            } else if (line.endsWith(SVN_MIME_TYPE)) {
                isMimeInfo = true;
            } else {
                newLines.add(line);
            }
        }
        return newLines;
    }
    
    protected static ArrayList<String> removeIgnoreInfoLines(List<String> lines) {
        ArrayList<String> newLines = new ArrayList<>();
        boolean isIgnoreInfo = false;
        int count = 0;
        for (String line : lines) {
            if (isIgnoreInfo == true) {
                count += getSvnAttributeInfoLinesCount(line, '-');
                count += getSvnAttributeInfoLinesCount(line, '+');
                isIgnoreInfo = false;
            } else if (0 < count) {
                count -= 1;
            } else if (line.endsWith(SVN_IGNRE)) {
                isIgnoreInfo = true;
            } else {
                newLines.add(line);
            }
        }
        return newLines;
    }

    private static int getSvnAttributeInfoLinesCount(String line, char attribute) {
        int count = 0;
        try {
            for (String str : line.split(" ")) {
                if (str.charAt(0) == attribute) {
                    int ints[] = Arrays.asList(str.split(",")).stream().mapToInt(Integer::parseInt).toArray();
                    int i = (ints.length < 2) ? ints[0] : ints[0] * ints[1];
                    count += (i < 0) ? i * -1 : i;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return (count < 0) ? 0 : count;
    }
}
