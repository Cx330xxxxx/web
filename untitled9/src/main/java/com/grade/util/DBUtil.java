package com.grade.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // 数据库连接参数，请根据你的实际 MySQL 配置修改！
    private static final String URL = "jdbc:mysql://localhost:3306/score_management?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4";
    private static final String USER = "root";      // 改为你的 MySQL 用户名
    private static final String PASSWORD = "ysy123456"; // 改为你的 MySQL 密码

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC 驱动加载失败，请检查是否添加了 mysql-connector-java 依赖", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}