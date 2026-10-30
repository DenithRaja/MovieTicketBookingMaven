package util;

import org.mindrot.jbcrypt.BCrypt;


public class PasswordUtil {

    private PasswordUtil() {}

    public static String hashPassword(String password) {
        if ((password == null) || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verifyPassword(String password,
                                         String hashedPassword) {
        if ((password == null) || (hashedPassword == null)) {
            return false;
        }

        return BCrypt.checkpw(password, hashedPassword);
    }
}