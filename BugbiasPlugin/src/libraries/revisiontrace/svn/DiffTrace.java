package libraries.revisiontrace.svn;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.wc2.ng.SvnDiffGenerator;
import org.tmatesoft.svn.core.wc2.SvnDiff;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public final class DiffTrace {

    private DiffTrace() {
    }

    protected static boolean outPutDiff(SvnPortal info, SvnDiffEntry entry, OutputStream output) {
        if (info == null || entry == null || !entry.getOldTarget().isPresent() || !entry.getNewTarget().isPresent()) {
            return false;
        }
        final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        info.getAuthenticate().ifPresent(auth -> svnOperationFactory.setAuthenticationManager(auth));
        try {
            SvnTarget root = SvnTarget.fromURL(info.getRemoteUrl().orElse(null));
            final SvnDiffGenerator diffGenerator = new SvnDiffGenerator();
            diffGenerator.setBasePath(new File(""));
            final SvnDiff diff = svnOperationFactory.createDiff();
            diffGenerator.setRepositoryRoot(root);
            diff.setSources(entry.getOldTarget().get(), entry.getNewTarget().get());
            diff.setDiffGenerator(diffGenerator);
            diff.setUseGitDiffFormat(true);
            diff.setOutput(output);
            diff.run();
            return true;
        } catch (SVNException e) {
            e.printStackTrace();
            return false;
        } finally {
            svnOperationFactory.dispose();
        }
    }

    private static final String DIFF_PREFIX = "SVN-DIFF-";

    protected static List<String> getDiffText(SvnPortal info, SvnDiffEntry entry) {
        if (info == null || entry == null) {
            return new ArrayList<>();
        }
        ArrayList<String> lines = new ArrayList<>();
        try {
            Path tmpDiff = Files.createTempFile(DIFF_PREFIX, null);
            try (OutputStream out = Files.newOutputStream(tmpDiff)) {
                outPutDiff(info, entry, out);
                out.flush();
            }
            Files.lines(tmpDiff).forEachOrdered(e -> lines.add(e));
            Files.delete(tmpDiff);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return lines;
    }
}
