package bugbias.main.internal;

import java.util.Optional;

import org.eclipse.swt.graphics.Color;

public class CautionLevel {
    
    public enum LEVEL{
        MINOR,
        MODERATE,
        HIGHLY
    }
    
    private CautionLevel() {
    }
    
    public static Optional<Color> getColor(LEVEL level) {
//        Display display = Display.getCurrent();
//        System.out.println(level);
//        switch (level) {
//        case MINOR:
//            return Optional.empty();
//        case MODERATE:
//            return Optional.of(display.getSystemColor(SWT.COLOR_YELLOW));
//        case HIGHLY:
//            return Optional.of(display.getSystemColor(SWT.COLOR_RED));
//        }
        return Optional.empty();
    }

}
