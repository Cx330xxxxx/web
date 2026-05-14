package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cs_demo";
    private static final String DB_USER = "root"; // 你的MySQL用户名
    private static final String DB_PASSWORD = "baijiaqi20031222"; // 替换为你的实际MySQL密码
    
    private Connection connection;
    
    public DatabaseManager() {
        try {
            // 加载JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("找不到MySQL JDBC驱动");
            e.printStackTrace();
        }
    }
    
    public boolean connect() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            return true;
        } catch (SQLException e) {
            System.err.println("数据库连接失败");
            e.printStackTrace();
            return false;
        }
    }
    
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean verifyUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean result = resultSet.next();
            
            resultSet.close();
            preparedStatement.close();
            
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ArrayList<HashMap<String, Object>> fetchData() {
        ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();
        
        try {
            String query = "SELECT * FROM data";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (resultSet.next()) {
                HashMap<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                dataList.add(row);
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return dataList;
    }
}