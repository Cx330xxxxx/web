package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 9876;
    private ServerSocket serverSocket;
    private DatabaseManager dbManager;
    private boolean isRunning = false;
    
    public Server() {
        dbManager = new DatabaseManager();
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("服务器启动，监听端口: " + PORT);
            
            if (dbManager.connect()) {
                System.out.println("数据库连接成功");
                isRunning = true;
                
                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("新的客户端连接: " + clientSocket.getInetAddress().getHostAddress());
                    
                    ClientHandler clientHandler = new ClientHandler(clientSocket, dbManager);
                    new Thread(clientHandler).start();
                }
            } else {
                System.err.println("无法连接到数据库，服务器关闭");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }
    
    public void stop() {
        isRunning = false;
        if (dbManager != null) {
            dbManager.disconnect();
        }
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
} 