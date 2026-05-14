package client;

import javax.swing.SwingUtilities;

import common.Message;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9876;
    
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private LoginGUI loginGUI;
    private Thread listenThread;
    
    public Client() {
        loginGUI = new LoginGUI(this);
        loginGUI.setVisible(true);
        
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            
            loginGUI.setConnected();
            
            listenThread = new Thread(this::listenForMessages);
            listenThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("无法连接到服务器: " + e.getMessage());
            loginGUI.updateLoginStatus(false, "无法连接到服务器");
        }
    }
    
    private void listenForMessages() {
        try {
            while (true) {
                Message message = (Message) input.readObject();
                
                switch (message.getType()) {
                    case LOGIN_RESPONSE:
                        handleLoginResponse(message);
                        break;
                    case DATA_RESPONSE:
                        handleDataResponse(message);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("与服务器连接断开: " + e.getMessage());
        }
    }
    
    public void login(String username, String password) {
        try {
            Message loginRequest = Message.createLoginRequest(username, password);
            output.writeObject(loginRequest);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            loginGUI.updateLoginStatus(false, "发送登录请求失败");
        }
    }
    
    public void requestData() {
        try {
            Message dataRequest = Message.createDataRequest();
            output.writeObject(dataRequest);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("请求数据失败: " + e.getMessage());
        }
    }
    
    private void handleLoginResponse(Message message) {
        boolean success = message.isSuccess();
        String resultMessage = message.getMessage();
        
        loginGUI.updateLoginStatus(success, resultMessage);
    }
    
    private void handleDataResponse(Message message) {
        ArrayList<HashMap<String, Object>> data = message.getData();
        loginGUI.updateDataTable(data);
    }
    
    public void disconnect() {
        try {
            if (listenThread != null) {
                listenThread.interrupt();
            }
            
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Client();
        });
    }
} 