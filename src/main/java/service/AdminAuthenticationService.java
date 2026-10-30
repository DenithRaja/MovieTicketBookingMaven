
package service;

public final class AdminAuthenticationService {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private static boolean adminLoggedIn = false;

    private AdminAuthenticationService() {
    }

    public static boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        if (ADMIN_USERNAME.equals(username.trim())
                && ADMIN_PASSWORD.equals(password)) {
            adminLoggedIn = true;
            return true;
        }

        return false;
    }

    public static boolean isAdminLoggedIn() {
        return adminLoggedIn;
    }

    public static void logout() {
        adminLoggedIn = false;
    }
}
