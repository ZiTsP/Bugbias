package bugbias.main.core.revision;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import libraries.putils.CodingLanguage;
import libraries.putils.CodingLanguages;
import libraries.putils.WeaklyEncryptions;

public class RevAnalyseConfig {

    public static RevAnalyseConfig getDeafult() {
        return DEFAULT_CONFIG;
    }
    
    public static final RevAnalyseConfig DEFAULT_CONFIG = new RevAnalyseConfig();
    
    private Map<CodingLanguage, Boolean> languages = new HashMap<>();
    private RevisionAnalysers.TYPE analyser;
    
    private RevAnalyseConfig() {
        CodingLanguages.getAll().forEach(e -> {
           languages.put(e, false);
        });
        this.setDefault();
    }
    
    private void setDefault() {
        languages.replace(CodingLanguages.JAVA, true);
        analyser = RevisionAnalysers.TYPE.FAULT_PRONE_FILTERING_CRM114;
    }
    
    public void update(Path xml) {
        
    }

    public void setLanguage(CodingLanguage language, boolean enable) {
        if (language != null) {
            languages.replace(language, enable);
        }
    }
    
    public List<CodingLanguage> getLanguages() {
        List<CodingLanguage> valids = new ArrayList<>();
        languages.forEach((k,v) -> {
            if (v = true) {
                valids.add(k);
            }
        });
        return valids;
    }
    
    public List<String> getExtensions() {
        List<String> valids = new ArrayList<>();
        this.getLanguages().stream().forEach(e -> valids.addAll(e.getExtension()));
        return valids.stream().distinct().collect(Collectors.toList());
    }
    
    public void setAnalyser(RevisionAnalysers.TYPE analyser) {
        if (analyser != null) {
            this.analyser = analyser;
        }
    }
    
    public RevisionAnalysers.TYPE getAnalyser() {
        return analyser;
    }
    
    
    private boolean autoEnable = false;
    private long autoCycle = 1000;

    public void setEnableAutoAnalyse(boolean enable) {
        autoEnable = enable;
    }
    
    public boolean isEnableAutoAnalyse(boolean enable) {
        return autoEnable;
    }

    public void setAutoAnalyseCycle(long milliSec) {
        autoCycle = milliSec;
    }
    
    public long getAutoAnalyseCycle() {
        return autoCycle;
    }

    private static final int HASH[] = WeaklyEncryptions.getRandomDecimalArray(16);
    private String auther;
    private int pass[];
    
    public void setAuthenticate(String auth, String pass) {
        if (auth != null) {
            this.auther = auth;
        }
        if (pass != null) {
            this.pass = WeaklyEncryptions.encryptHashedCaesar(pass, HASH);
        }
    }
    
    public Optional<String> getAuther() {
        return Optional.ofNullable(this.auther);
    }
    
    public Optional<String> getPassword(String auth) {
        if (auth.equals(this.auther)) {
            return (pass != null) ? Optional.of(WeaklyEncryptions.decryptHashedCaesar(pass, HASH)) : Optional.of("");
        }
        return Optional.empty();
    }
    
    private RevWatchingConfig revWatchingConfig = RevWatchingConfig.DEFAULT_CONFIG;
    
    public RevWatchingConfig getWatchConfig() {
        return this.revWatchingConfig;
    }
}
