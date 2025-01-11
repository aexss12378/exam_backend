package Manager;

import java.util.HashMap;
import java.util.Map;

public class AccountManager {
    private final Map<String, Map<String, String>> userDatabase = new HashMap<>();


    // constructor
    public AccountManager() {

        // 預設帳戶
        Map<String, String> adminDetails = new HashMap<>();
        adminDetails.put("password", "admin123");
        adminDetails.put("role", "admin");
        userDatabase.put("admin", adminDetails);

        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("password", "user123");
        userDetails.put("role", "user");
        userDatabase.put("user", userDetails);
    }

    // 驗證使用者名稱與密碼是否正確
    public boolean authenticate(String username, String password) {
        if (userDatabase.containsKey(username)) {
            Map<String, String> userDetails = userDatabase.get(username);
            String storedPassword = userDetails.get("password");
            return storedPassword != null && storedPassword.equals(password);
        }
        return false;
    }

    // 根據使用者名稱取得帳戶資訊
    public Map<String, String> getAccountInfo(String username) {
        if (!userDatabase.containsKey(username)) {
            throw new IllegalArgumentException("User not found: " + username);
        }

        Map<String, String> info = new HashMap<>();
        info.put("username", username);
        info.put("role", username.equals("admin") ? "administrator" : "standard_user"); // 簡單區分角色
        return info;
    }

    // 新增帳戶 (註冊要三個，多一個role)
    public String createAccount(String username, String password, String role) {
        // 檢查是否已經存在該使用者
        if (userDatabase.containsKey(username)) {
            return "Account creation failed: Username already exists.";
        }

        // 檢查角色是否合法
        if (!role.equals("teacher") && !role.equals("student")) {
            return "Account creation failed: Invalid role. Please choose 'teacher' or 'student'.";
        }

        // 創建帳號，並存儲角色和密碼
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("password", password);
        userDetails.put("role", role);

        userDatabase.put(username, userDetails);
        return "Account created successfully for username: " + username + " with role: " + role;
    }
}