package libraries.putils;

public class WeaklyEncryptions {

    private WeaklyEncryptions() {
    }
    
    public static int[] getRandomDecimalArray(int length) {
        int i;
        int array[] = new int[length];
        for (i = 0; i < length; i++) {
            array[i] = (int) (Math.random() * 10);
        }
        return array;
//        return IntStream.rangeClosed(0, length).map(i -> (int) Math.random() * 10).toArray(); Darkness of Stream
    }

    public static int[] encryptHashedCaesar(String text, int[] hash) {
        int[] me = text.chars().toArray();
        return encryptHashedCaesar(me, hash);
    }
    
    public static int[] encryptHashedCaesar(int[] chars, int[] hash) {
        int i;
        int array[] = new int[chars.length];
        for (i = 0; i < chars.length; i++) {
            array[i] = ((i < hash.length) ?  (chars[i] + hash[i]) : (chars[i] + hash[i - hash.length]));
        }
        return array;
//        return IntStream.range(0, chars.length).map(i -> (i < hash.length) ?  chars[i] + hash[i] : chars[i] + hash[i - hash.length]).toArray(); Darkness of Stream
    }
    
    public static String decryptHashedCaesar(int[] cryptogram, int[] hash) {
        int i;
        char array[] = new char[cryptogram.length];
        for (i = 0; i < cryptogram.length; i++) {
            array[i] = (char) ((i < hash.length) ?  (cryptogram[i] - hash[i]) : (cryptogram[i] - hash[i - hash.length]));
        }
        return String.valueOf(array);
    }
    
}