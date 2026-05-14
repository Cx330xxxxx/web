package server;

import common.Message;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DatabaseManager dbManager;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String clientAddress;
    
    public ClientHandler(Socket socket, DatabaseManager dbManager) {
        this.clientSocket = socket;
        this.dbManager = dbManager;
        this.clientAddress = clientSocket.getInetAddress().getHostAddress();
        
        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            System.out.println("用户/" + clientAddress + "连接主机。数据库连接成功！");
            
            while (true) {
                Message message = (Message) input.readObject();
                
                switch (message.getType()) {
                    case LOGIN_REQUEST:
                        handleLoginRequest(message);
                        break;
                    case DATA_REQUEST:
                        handleDataRequest();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("用户/" + clientAddress + "断开连接");
        } finally {
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handleLoginRequest(Message message) throws IOException {
        String username = message.getUsername();
        String password = message.getPassword();
        
        boolean success = dbManager.verifyUser(username, password);
        String responseMessage = success ? "登录成功" : "登录失败";
        
        Message response = Message.createLoginResponse(success, responseMessage);
        output.writeObject(response);
        output.flush();
    }
    
    private void handleDataRequest() throws IOException {
        try {
            ArrayList<HashMap<String, Object>> data = dbManager.fetchData();
            System.out.println("服务器获取到 " + data.size() + " 条数据记录"); // 调试信息
            
            Message response = Message.createDataResponse(data);
            output.writeObject(response);
            output.flush();
            System.out.println("数据响应已发送给客户端");
        } catch (Exception e) {
            System.err.println("处理数据请求时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}