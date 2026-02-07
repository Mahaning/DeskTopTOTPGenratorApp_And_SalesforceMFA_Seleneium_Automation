package TOTP;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TotpSecretStore {

    private static Properties props = new Properties();

    static {
        try (InputStream input = TotpSecretStore.class.getClassLoader()
                .getResourceAsStream("users.properties")) { // resource path
            if (input == null) {
                System.err.println("Unable to find users.properties");
            } else {
                props.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSecret(String username) {
        return props.getProperty(username);
    }
}
