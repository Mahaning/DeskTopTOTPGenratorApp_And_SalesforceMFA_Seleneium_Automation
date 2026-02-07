package TotpDesktopApp;

import java.io.InputStream;
import java.util.Properties;

public final class UserSecretStore {

    private static final Properties USERS = new Properties();

    static {
        try (InputStream in =
                     UserSecretStore.class.getResourceAsStream("/users.properties")) {

            if (in == null) {
                throw new RuntimeException("users.properties not found in classpath");
            }
            USERS.load(in);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load users", e);
        }
    }

    private UserSecretStore() {}

    public static String[] getUsers() {
        return USERS.stringPropertyNames().toArray(new String[0]);
    }

    public static String getSecret(String user) {
        return USERS.getProperty(user);
    }
}
