package libraries.revisiontrace.git;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import libraries.putils.Charsets;

public final class DiffTrace {
    
    private DiffTrace() {
    }

    protected static List<DiffEntry> trimExtraExtensionFiles(List<DiffEntry> diffEntries, String extension) {
        if (diffEntries == null) {
            return new ArrayList<>();
        } else if (extension == null || extension.equals("")) {
            return diffEntries;
        }
        List<DiffEntry> list = new ArrayList<>();
        diffEntries.stream().filter(e -> e.getNewPath().endsWith(extension)).forEach(e -> list.add(e));
        return list;
    }
    
    protected static boolean outputDiff(DiffEntry diffEntry, Path gitDir, OutputStream outStream) {
        if (diffEntry == null) {
            return false;
        }
        try (Repository repository = new FileRepositoryBuilder().setGitDir(gitDir.toFile()).build();
                DiffFormatter diffFormatter = new DiffFormatter(outStream)) {
            diffFormatter.setRepository(repository);
            diffFormatter.format(diffEntry);
            return true;
        } catch (MissingObjectException exception) {
            if (diffEntry.getChangeType().equals(ChangeType.ADD)) {
                Path newPath = Paths.get(gitDir.toAbsolutePath().getParent().toString(), diffEntry.getNewPath());
                try (BufferedReader reader = Files.newBufferedReader(newPath, Charsets.getDefault())) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String addLine = new StringBuffer("+").append(line).toString();
                        outStream.write(addLine.getBytes());
                    }
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    private static final int PIPEDOUTPUTSTREAM_BUFFER_SIZE = 512 * 100;
    
    protected static ArrayList<String> getDiffText(DiffEntry diffEntry, Path gitDir) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            PipedOutputStream pout = new PipedOutputStream();
            OutputStream out = new BufferedOutputStream(pout, PIPEDOUTPUTSTREAM_BUFFER_SIZE);
            PipedInputStream pin = new PipedInputStream(pout);
            BufferedReader in = new BufferedReader(new InputStreamReader(pin));
            outputDiff(diffEntry, gitDir, out);
            out.write(0);
            out.flush();
            out.close();
//            pout.close();
            String line;
            while((line = in.readLine()) != null) {
                lines.add(line);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

}
