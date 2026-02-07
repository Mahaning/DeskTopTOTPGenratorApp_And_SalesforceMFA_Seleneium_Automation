package TotpDesktopApp;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class RFC6238TOTP {

    private static final int intTIME_STEP = 30;
    private static final int intDIGITS = 6;
    private static final String strHMAC = "HmacSHA1";

    private RFC6238TOTP() {}

    public static String generate(String base32Secret) {
        long counter = Instant.now().getEpochSecond() / intTIME_STEP;
        return generateOtp(base32Decode(base32Secret), counter);
    }

    /* Core TOTP algorithm */
    private static String generateOtp(byte[] key, long lngCounter) {
        try {
            byte[] counterBytes = ByteBuffer.allocate(8).putLong(lngCounter).array();
            Mac mac = Mac.getInstance(strHMAC);
            mac.init(new SecretKeySpec(key, strHMAC));
            byte[] hash = mac.doFinal(counterBytes);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary =
                    ((hash[offset] & 0x7F) << 24) |
                    ((hash[offset + 1] & 0xFF) << 16) |
                    ((hash[offset + 2] & 0xFF) << 8) |
                    (hash[offset + 3] & 0xFF);

            int otp = binary % (int) Math.pow(10, intDIGITS);
            return String.format("%06d", otp);

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /* Base32 decoder (RFC 4648) */
    private static byte[] base32Decode(String base32) {
        base32 = base32.replace("=", "").toUpperCase();
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

        ByteBuffer buffer = ByteBuffer.allocate(base32.length() * 5 / 8);
        int bits = 0, value = 0;

        for (char c : base32.toCharArray()) {
            int intIndex = alphabet.indexOf(c);
            if (intIndex < 0) {
                throw new IllegalArgumentException("Invalid Base32 character: " + c);
            }

            value = (value << 5) | intIndex;
            bits += 5;

            if (bits >= 8) {
                buffer.put((byte) ((value >> (bits - 8)) & 0xFF));
                bits -= 8;
            }
        }

        byte[] result = new byte[buffer.position()];
        buffer.flip();
        buffer.get(result);
        return result;
    }
}
