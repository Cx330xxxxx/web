package com.grade.dao;

import com.grade.model.User;
import com.grade.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    /**
     * 根据用户名、密码和角色查询用户（登录验证）
     * @param username 学号或工号
     * @param password 密码
     * @param role 角色（student/teacher）
     * @return 查询到的用户对象，如果未找到则返回 null
     */
    public User login(String username, String password, String role) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, username, password, role, name FROM users " +
                    "WHERE username = ? AND password = ? AND role = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setName(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            DBUtil.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}