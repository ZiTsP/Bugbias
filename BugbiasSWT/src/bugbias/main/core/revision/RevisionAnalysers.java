package bugbias.main.core.revision;

import java.io.IOException;
import java.nio.file.Path;
import java.util.OptionalDouble;

import org.eclipse.swt.graphics.Color;

import bugbias.analyse.faultpronefiltering.FaultProneFilteringCRM114;

public class RevisionAnalysers {
    
    public enum TYPE {
        FAULT_PRONE_FILTERING_CRM114,
        FAULT_PRONE_FILTERING_OVERHAULED_OSBF,
    }

    private RevisionAnalysers() {
    }

    public static void init(Path configDir) {
        try {
            FaultProneFilteringCRM114.init(configDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static OptionalDouble analyse(Path path, TYPE type) {
        try {
            switch (type) {
                case FAULT_PRONE_FILTERING_CRM114:
                    return FaultProneFilteringCRM114.classify(path);
                case FAULT_PRONE_FILTERING_OVERHAULED_OSBF:
                    return OptionalDouble.of(0.2);
                default:
                    System.out.println("ANALYSE ERR");
                    return OptionalDouble.empty();
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return OptionalDouble.empty();
    }
    
    public static boolean learn(Path path, boolean isAccurate, TYPE type) {
        try {
            switch (type) {
            case FAULT_PRONE_FILTERING_CRM114:
                System.out.println("LEARN CRM114");
                return FaultProneFilteringCRM114.learn(path, isAccurate);
            case FAULT_PRONE_FILTERING_OVERHAULED_OSBF:
                return false;
            default:
                System.out.println("LEARN ERR");
                return false;
            } 
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static Color checkCaution(double result, TYPE type) {
        switch (type) {
        case FAULT_PRONE_FILTERING_CRM114:
            return FaultProneFilteringCRM114.getCautionColor(result).orElse(null);
        case FAULT_PRONE_FILTERING_OVERHAULED_OSBF:
        default:
            return null;
        }
    }
}
