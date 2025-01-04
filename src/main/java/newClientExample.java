public class newClientExample {
    public static void main(String[] args) {
        try {
            // 建立 Client 實例
            ClientServerCommunicator client = new ClientServerCommunicator("http://localhost:8080");
            
            // // GET 請求範例
            // String getResponse = client.sendGetRequest("/exam");
            // System.out.println("GET Response: " + getResponse);

            // 創建帳戶
            System.out.println("\n====== 創建新帳戶 ======");
            String newAccount = "{\"username\": \"test_user\", \"password\": \"1234\"}";
            String newAccountResponse = client.sendPostRequest("/account", newAccount);
            System.out.println("POST Response: " + newAccountResponse);
            
            // POST 請求範例
            // String jsonPayload = "{\"key\":\"value\"}";
            // String postResponse = client.sendPostRequest("/paper", jsonPayload);
            // System.out.println("POST Response: " + postResponse);

            // 創建新考試
            // System.out.println("\n====== 創建新考試 ======");
            // String newExam = "{\"name\": \"Java 考試\", \"score\": 100}";
            // String newExamResponse = client.sendPostRequest("/exam", newExam);
            // System.out.println(newExamResponse);

            // 取得考試列表
            // System.out.println("\n====== 取得考試列表 ======");
            // String examList = client.sendGetRequest("/exam");
            // System.out.println(examList);
            
            // 取得考試資訊
            // System.out.println("\n====== 取得考試資訊 ======");
            // String examInfo = client.sendGetRequest("/exam/1");
            // System.out.println(examInfo);
            
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
