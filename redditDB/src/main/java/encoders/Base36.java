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

    /**
     * Converts a string with a number in base 10 into base 36.
     * @param base10 The number encoded as a string in base 10.
     * @return The number encoded as a string in base 36.
     */
    public static String toBase36(String base10) {
        if (base10.length() > 18) {
            return new BigInteger(base10).toString(radix);
        }
        return toBase36(Long.parseLong(base10));
    }

    /**
     * Converts 'smaller' numbers (i.e. fits in long) to base36 encoded strings.
     * @param base10 A long to encode.
     * @return A string that has the number in base 36.
     */
    public static String toBase36(long base10) {
        StringBuilder encoded = new StringBuilder();
        int maxPos = 0;
        while (base10 > positions[maxPos + 1]) maxPos++;
        while (base10 > 0) {
            int posVal = (int) (base10 / positions[maxPos]); // Will always fit in int
            encoded.append(Character.forDigit(posVal, radix));
            base10 = base10 - posVal * positions[maxPos];
            maxPos--;
        }
        return encoded.toString();
    }

    /**
     * Converts from a string encoded in base 36 to a string in base 10.
     *
     * @param base36 A base 36 number as a string.
     * @return The string as a base 10 number.
     */
    public static String fromBase36(String base36) {
        try {
            BigInteger base = new BigInteger(base36, radix);
            return base.toString(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static long longFromBase36(String base36) {
        if (base36.length() > positions.length) return Long.MIN_VALUE;
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
