package libraries.putils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class EpocDateTime {

	private EpocDateTime(){
	}

	private static final ZoneOffset ZONE = ZonedDateTime.now().getOffset();
	
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private static final long getEpochSecond(long time) {
        return time/1000;
    }

    private static final int getEpochMilliSec(long time) {
        long nano = time%100;
        return (int) nano;
    }

    public static final LocalDateTime convertEpocToDateTime(long epoc) {
        return  LocalDateTime.ofEpochSecond(getEpochSecond(epoc), getEpochMilliSec(epoc), ZONE);
    }
    
    public static final LocalDateTime convertEpocToDateTime(int epoc) {
        return  LocalDateTime.ofEpochSecond(epoc, 0, ZONE);
    }
    
    public static final LocalDateTime convertEpocMilliSecToDateTime(long epoc) {
        return  LocalDateTime.ofEpochSecond(getEpochSecond(epoc), getEpochMilliSec(epoc), ZONE);
    }
    
    public static final LocalDateTime convertEpocSecToDateTime(int epoc) {
        return  LocalDateTime.ofEpochSecond(epoc, 0, ZONE);
    }
    
    public static final String convertEpocMilliSecToString(long epoc) {
        return  convertEpocMilliSecToDateTime(epoc).format(formatter);
    }
    public static final String convertEpocSecToString(int epoc) {
        return  convertEpocSecToDateTime(epoc).format(formatter);
    }

    public static final long now() {
        return ZonedDateTime.now().toEpochSecond() * 1000;
    }
    
    public static final String nowToString() {
        return convertEpocMilliSecToString(ZonedDateTime.now().toEpochSecond() * 1000);
    }
}
