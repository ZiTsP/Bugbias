package libraries.revisiontrace.svn;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.wc2.ng.SvnDiffGenerator;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.SvnDiff;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public final class DiffTrace {
    
    private DiffTrace() {
    }

    protected static boolean outPutDiff(SvnPortal info, String filePath, long oldRevNum, long newRevNum, OutputStream output) {
        final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        svnOperationFactory.setAuthenticationManager(info.getAuthenticate().get());
        try {
            SvnTarget root = SvnTarget.fromURL(info.getUrl().get());
            SVNURL fileUrl = SVNURL.parseURIEncoded(filePath);
            SVNRevision oldRev = SVNRevision.create(oldRevNum);
            SVNRevision newRev = SVNRevision.create(newRevNum);
            SvnTarget target = SvnTarget.fromURL(fileUrl);
            final SvnDiffGenerator diffGenerator = new SvnDiffGenerator();
            diffGenerator.setBasePath(new File(""));
            final SvnDiff diff = svnOperationFactory.createDiff();
            diffGenerator.setRepositoryRoot(root);
            diff.setSource(target, oldRev, newRev);
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
    
    protected static boolean outPutWorkingDiff(SvnPortal info,String filePath, OutputStream output) {
        final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        svnOperationFactory.setAuthenticationManager(info.getAuthenticate().get());
        try {
            SvnTarget root = SvnTarget.fromURL(info.getUrl().get());
            SVNURL fileUrl = SVNURL.parseURIEncoded(filePath);
            SVNRevision oldRev = SVNRevision.HEAD;
            SVNRevision newRev = SVNRevision.WORKING;
            SvnTarget target = SvnTarget.fromURL(fileUrl);
            final SvnDiffGenerator diffGenerator = new SvnDiffGenerator();
            diffGenerator.setBasePath(new File(""));
            final SvnDiff diff = svnOperationFactory.createDiff();
            diffGenerator.setRepositoryRoot(root);
            diff.setSource(target, oldRev, newRev);
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
    
    protected static boolean getFileDiff(SvnPortal info, String filePath ,long oldRevNum, long newRevNum, OutputStream output) {
        final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        svnOperationFactory.setAuthenticationManager(info.getAuthenticate().get());
        try {
            SvnTarget root = SvnTarget.fromURL(info.getUrl().get());
            SVNRevision oldRev = SVNRevision.create(oldRevNum);
            SVNRevision newRev = SVNRevision.create(newRevNum);
            SVNURL fileUrl = info.getUrl().get().appendPath(filePath, true);
            SvnTarget target = SvnTarget.fromURL(fileUrl);
            final SvnDiffGenerator diffGenerator = new SvnDiffGenerator();
            diffGenerator.setBasePath(new File(""));
            final SvnDiff diff = svnOperationFactory.createDiff();
            diffGenerator.setRepositoryRoot(root);
            diff.setSource(target, oldRev, newRev);
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

    private static final int PIPEDOUTPUTSTREAM_BUFFER_SIZE = 512 * 100;
    protected static List<String> getDiffText(SvnPortal info, SvnDiffEntry entry) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            PipedOutputStream pout = new PipedOutputStream();
            OutputStream out = new BufferedOutputStream(pout, PIPEDOUTPUTSTREAM_BUFFER_SIZE);
            PipedInputStream pin = new PipedInputStream(pout);
            BufferedReader in = new BufferedReader(new InputStreamReader(pin));
            if (entry.getRevision().equals(SVNRevision.WORKING)) {
                outPutDiff(info, entry.getFilePath(), SVNRevision.HEAD, SVNRevision.WORKING, out);
            } else if (1 < entry.getRevisionAsLong()){
                outPutDiff(info, entry.getFilePath(), entry.getRevisionAsLong() - 1, entry.getRevisionAsLong(), out);
            }
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

    protected static boolean outPutDiff(SvnPortal info, String filePath, SVNRevision oldRev, SVNRevision newRev, OutputStream output) {
        final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        svnOperationFactory.setAuthenticationManager(info.getAuthenticate().get());
        try {
            SvnTarget root = SvnTarget.fromURL(info.getUrl().get());
            SVNURL fileUrl = SVNURL.parseURIEncoded(filePath);
            SvnTarget target = SvnTarget.fromURL(fileUrl);
            final SvnDiffGenerator diffGenerator = new SvnDiffGenerator();
            diffGenerator.setBasePath(new File(""));
            final SvnDiff diff = svnOperationFactory.createDiff();
            diffGenerator.setRepositoryRoot(root);
            diff.setSource(target, oldRev, newRev);
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
}
