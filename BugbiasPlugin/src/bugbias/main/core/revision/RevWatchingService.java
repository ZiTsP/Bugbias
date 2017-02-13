package bugbias.main.core.revision;

import java.nio.file.Path;
import java.util.Optional;

import bugbias.main.internal.TreeData;
import bugbias.main.widget.RevisionTable;

public class RevWatchingService extends Thread {
    
    private final Optional<Path> revDir;
    private final Optional<IAnalyseRevisionBridge> revBridge;
    private final Optional<RevisionTable> revTable;

    public RevWatchingService(RevAnalyseCore core) {
        this.revDir = core.getRootPath();
        this.revBridge = core.getRevisionBridge();
        this.revTable = Optional.ofNullable(core.getMainWidget());
    }
    
    public RevWatchingService(RevAnalyseCore core, RevWatchingConfig config) {
        this(core);
        loadConfig(config);
    }
    
    public void loadConfig(RevWatchingConfig config) {
        this.doze = config.getUpdateAnalyseInterval();
        this.nap = config.getPeriodAnalyseInterval();
    }
    
    private boolean kill = false;
    
    private long nap = 180000L; //30min
    private final long napInterval = RevWatchingConfig.SLEEP_LONG;
    private long napCount = 0;
    private long dozeCount = 0;
    
    public void resetSleep() {
        napCount = 0;
        dozeCount = 0;
    }
    
    private long doze;

    private Optional<Object> head = Optional.empty();
    
    @Override
    public void run() {
        if (revDir.isPresent() && revBridge.isPresent()) {
            dozeCount = doze;
                while (!kill) {
                    try {
                    if (doze <= dozeCount || nap <= napCount) {
                        if (doze <= dozeCount) {
                            dozeCount = 0;
                            Optional<Object> newHead = revBridge.get().getLastRevision();
                            if (newHead.isPresent() && !head.equals(newHead)){
                                this.head = newHead;
                                Optional<TreeData> data = revBridge.get().analyseLastRevision();
                                if (data.isPresent() && this.revTable.isPresent()) {
                                    this.revTable.get().addInput(data.get());
                                }
                            }
                        }
                        if (nap <= napCount) {
                            napCount = 0;
                            Optional<TreeData> data = revBridge.get().analyseCurrentRevision();
                            if (data.isPresent() && this.revTable.isPresent()) {
                                this.revTable.get().addInput(data.get());
                            }
                        }
                    } else {
                        Thread.sleep(napInterval);
                        dozeCount += napInterval;
                        napCount += napInterval;
                    }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
    
    public void kill() {
        this.kill = true;
    }

    public void setSleepMinutes(long min) {
        long millisec = min * 6000;
        this.nap = (0 < millisec) ? millisec : this.nap;
    }
    
    public void setSleepSeconds(long sec) {
        long millisec = sec * 100;
        this.nap = (0 < millisec) ? millisec : this.nap;
    }
}
