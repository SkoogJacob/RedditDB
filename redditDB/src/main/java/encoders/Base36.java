package encoders;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Base36 {
    private static final int radix = 36;
    /**
     * Saves the values a digit in position 0-13 would hold. I.e., a digit at
     * String.length - 1 would have value Long.parse(String.charAt(String.length -1) * positions[0]
     */
    private static final long[] positions = {1, 36, 1296, 46_656, 1_679_616, 60_466_176,
            2_176_782_336L, 78_364_164_096L, 2_821_109_907_456L, 101_559_956_668_416L,
            3_656_158_440_062_976L, 131_621_703_842_267_136L, 4_738_381_338_321_616_896L};
    private static final long eleventhPos = 3_656_158_440_062_976L;

    public static String toBase36(String base10) {
        if (base10.length() > positions.length) {
            byte[] bytes = base10.getBytes(StandardCharsets.UTF_8);
            return new BigInteger(1, bytes).toString(radix);
        }
        return toBase36(Long.parseLong(base10));
    }
    public static String toBase36(long base10) {
        String encoded = ""; // using string instead of StringBuilder as the string here will be short (max 13 characters)
        int maxPos = 0;
        while (base10 > positions[maxPos]) maxPos++;
        maxPos--;
        while (base10 > 0) {
            int posVal = (int) (base10 / positions[maxPos]); // Will always fit in int
            encoded += Character.forDigit(posVal, radix);
            base10 = base10 - posVal * positions[maxPos];
            maxPos--;
        }
        return encoded;
    }
    public static String stringFromBase36(String base36) {
        try {
            BigInteger base = new BigInteger(base36, radix);
            return base.toString(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static long longFromBase36(String base36) {
        char[] chars = base36.toCharArray();
        int length = chars.length;
        long number = 0;
        for (int i = 0; i < length; i++) {
            long value = Character.digit(chars[i], radix);
            number += (long) (value * Math.pow(radix, length - 1 - i));
        }
        return number;
    }
}
