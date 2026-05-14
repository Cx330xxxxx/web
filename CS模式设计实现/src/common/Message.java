package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Type {
        LOGIN_REQUEST,
        LOGIN_RESPONSE,
        DATA_REQUEST,
        DATA_RESPONSE
    }
    
    private Type type;
    private String username;
    private String password;
    private boolean success;
    private String message;
    private ArrayList<HashMap<String, Object>> data;
    
    // 构造函数
    public Message(Type type) {
        this.type = type;
    }
    
    // 登录请求
    public static Message createLoginRequest(String username, String password) {
        Message msg = new Message(Type.LOGIN_REQUEST);
        msg.username = username;
        msg.password = password;
        return msg;
    }
    
    // 登录响应
    public static Message createLoginResponse(boolean success, String message) {
        Message msg = new Message(Type.LOGIN_RESPONSE);
        msg.success = success;
        msg.message = message;
        return msg;
    }
    
    // 数据请求
    public static Message createDataRequest() {
        return new Message(Type.DATA_REQUEST);
    }
    
    // 数据响应
    public static Message createDataResponse(ArrayList<HashMap<String, Object>> data) {
        Message msg = new Message(Type.DATA_RESPONSE);
        msg.data = data;
        return msg;
    }
    
    // Getters
    public Type getType() { return type; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ArrayList<HashMap<String, Object>> getData() { return data; }
} 