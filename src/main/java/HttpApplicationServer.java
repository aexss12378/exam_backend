import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import handlers.*;

public class HttpApplicationServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("HTTP Server started on port " + port);

        // 註冊端點endpoints
        server.createContext("/exam", new ExamHandler("D:\\oop testfile\\test.xlsx"));
        server.createContext("/paper", new PaperHandler());
        server.createContext("/account", new AccountHandler());
        server.createContext("/static", new StaticHandler());
        
        // 設置固定大小的執行器，最多允許 10 個線程
        ExecutorService executor = Executors.newFixedThreadPool(10);
        server.setExecutor(executor);
        server.start();
    }
}