## ClientServerCommunicator
設計ClientServerCommunicator的建構子，需要傳入serverUrl
```=java
public ClientServerCommunicator(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
```
### Define GET Method：
向指定的伺服器端點（endpoint）發送 HTTP GET 請求並處理回應
```=java
public String sendGetRequest(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + endpoint))
                .GET() 
                .header("Accept", "application/json") //接受 JSON 格式的回應
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new Exception("GET request failed with response code: " + response.statusCode());
        }
    }
```
### Define POST Method：
將資料（payload）傳送到伺服器端點並處理伺服器的回應
```=java
public String sendPostRequest(String endpoint, String payload) throws Exception {
        // 1. 建立 HttpRequest    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(payload))// 設定為 POST 並傳送字串形式的資料
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new Exception("POST request failed with response code: " + response.statusCode());
        }
    }
```
## How to use it? Implementation !
### 1. GET


---

首先Client端先實作ClientServerCommunicator。
```=java
ClientServerCommunicator client = new ClientServerCommunicator("http://localhost:8080");
```
此處的client即為ClientServerCommunicator的實例，需傳入一個serverUrl
下一步，client可以使用sendGetRequest這個function，指定一個endpoint，就會得到該endpoint的回傳值
```=
String getResponse = client.sendGetRequest("/exam");
            System.out.println("GET Response: " + getResponse);
```
### 2. POST
---
```=
String jsonPayload = "{\"key\":\"value\"}";
            String postResponse = client.sendPostRequest("/paper", jsonPayload);
            System.out.println("POST Response: " + postResponse);
```