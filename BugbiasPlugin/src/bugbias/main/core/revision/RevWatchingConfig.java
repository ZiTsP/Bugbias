package bugbias.main.core.revision;

public class RevWatchingConfig {
    
    public static final RevWatchingConfig DEFAULT_CONFIG = new RevWatchingConfig();

    public RevWatchingConfig() {
    }

    public static final long SLEEP_LONG = 100L; // 1min
    
    private long periodAnalyseInterval = DEFAULT_PERIOD_ANALYSE_LONG;
    private static final long DEFAULT_PERIOD_ANALYSE_LONG = 18000L; // 30min
    private static final long MAX_PERIOD_ANALYSE_LONG = 1440000L; //4hour
    
    private final boolean isValidPeriodAnalyseInterval(long milliSec) {
        return (SLEEP_LONG <= milliSec && milliSec <= MAX_PERIOD_ANALYSE_LONG);
    }
    public void setPeriodAnalyseInterval(long milliSec) {
        this.periodAnalyseInterval = (isValidPeriodAnalyseInterval(milliSec)) ? milliSec : this.periodAnalyseInterval;
    }
    public void setDefaultPeriodAnalyseInterval() {
        this.periodAnalyseInterval = DEFAULT_PERIOD_ANALYSE_LONG;
    }
    
    public long getPeriodAnalyseInterval() {
        return this.periodAnalyseInterval;
    }
    
    private long updateAnalyseInterval = DEFAULT_UPDATE_ANALYSE_LONG;
    private static final long DEFAULT_UPDATE_ANALYSE_LONG = 3000L; // 5min
    private static final long MAX_UPDATE_ANALYSE_LONG = 360000L; //1hour

    private final boolean isValidUpdateAnalyseInterval(long milliSec) {
        return (SLEEP_LONG <= milliSec && milliSec <= MAX_UPDATE_ANALYSE_LONG);
    }
    public void setUpdateAnalyseInterval(long milliSec) {
        this.updateAnalyseInterval = (isValidUpdateAnalyseInterval(milliSec)) ? milliSec : this.periodAnalyseInterval;
    }
    public void setDefaultUpdateAnalyseInterval() {
        this.updateAnalyseInterval = DEFAULT_UPDATE_ANALYSE_LONG;
    }
    
    public long getUpdateAnalyseInterval() {
        return this.updateAnalyseInterval;
    }
    
}
