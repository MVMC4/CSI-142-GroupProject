import java.util.UUID;
import java.util.Random;
import java.lang.StringBuilder;

public class IdGenerator {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();
    
    public static String generateShortId(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
    
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
    
    public static long generateNumericId(int digits) {
        return (long) (Math.random() * Math.pow(10, digits));
    }
}
