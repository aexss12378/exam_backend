package Manager;

import java.util.HashMap;
import java.util.Map;

// public class AccountManager { //write by 王凱葳
//     private Map<String, String> userDatabase;

//     public AccountManager() {
//         this.userDatabase = new HashMap<>();
//         // Add some test users
//         userDatabase.put("admin", "admin123");
//         userDatabase.put("user", "user123");
//     }

//     public boolean authenticate(String username, String password) {
//         return userDatabase.containsKey(username) && userDatabase.get(username).equals(password);
//     }
// }

public class AccountManager {
    private Map<String, String> userDatabase = new HashMap<>();

    // constructor
    public AccountManager() {
        this.userDatabase = new HashMap<>();
        // 預設帳戶
        userDatabase.put("admin", "admin123");
        userDatabase.put("user", "user123");
    }

    // 驗證使用者名稱與密碼是否正確
    public boolean authenticate(String username, String password) {
        return userDatabase.containsKey(username) && userDatabase.get(username).equals(password);
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
    public String createAccount(String username, String password) {
        if (userDatabase.containsKey(username)) {
            return "Account creation failed: Username already exists.";
        }

        userDatabase.put(username, password);
        return "Account created successfully for username: " + username;
    }
}