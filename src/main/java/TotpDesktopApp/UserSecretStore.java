package TotpDesktopApp;

import java.io.InputStream;
import java.util.Properties;

public final class UserSecretStore {

    // Load user secrets from properties file
    private static final Properties USERS = new Properties();

    // Static initializer to load user secrets at class loading time
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

    // Private constructor to prevent instantiation
    private UserSecretStore() {}

    // ================= API =================
    // Get all usernames available in the store
    public static String[] getUsers() {
        return USERS.stringPropertyNames().toArray(new String[0]);
    }

    // Get the secret key for a given user, or null if user not found
    public static String getSecret(String user) {
        return USERS.getProperty(user);
    }
}
