package TOTP;

import java.time.Instant;
 
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
 
import de.taimos.totp.TOTP;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.exceptions.CodeGenerationException;
 
public class TOTPGenerator {
    public String generateTotp(String strSecret) {
        String key = strSecret;    // this is the secret key that's presented during the test user setup
        System.out.println("Taimos: " + taimos(key));
        System.out.println("java-totp: " + javaTotp(key));
        return javaTotp(key).toString();
    }
 
    // using https://github.com/taimos/totp
    private String taimos(String key) {
        // the key needs to be Base32 encoded
        final Base32 base32 = new Base32();
        final byte[] bytes = base32.decode(key);
        final String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }
 
    // using https://github.com/samdjstevens/java-totp
    private String javaTotp(String key) {
        DefaultCodeGenerator generator = new DefaultCodeGenerator();
        try {
            // no encoding of the key necessary
            return generator.generate(key, Math.floorDiv(Instant.now().getEpochSecond(), 30L));
        } catch (CodeGenerationException e) {
            throw new RuntimeException(e);
        }
    }
}