package com.grade.dao;

import com.grade.model.Grade;
import com.grade.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDao {

    /**
     * 查询某个学生的所有成绩
     * @param studentId 学号
     * @return 成绩列表
     */
    public List<Grade> findByStudentId(String studentId) {
        List<Grade> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT id, student_id, course_name, score, semester FROM grades WHERE student_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Grade grade = new Grade();
                grade.setId(rs.getInt("id"));
                grade.setStudentId(rs.getString("student_id"));
                grade.setCourseName(rs.getString("course_name"));
                grade.setScore(rs.getDouble("score"));
                grade.setSemester(rs.getString("semester"));
                list.add(grade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 查询所有学生的全部成绩，并关联学生姓名
     * @return 成绩列表（包含学生姓名）
     */
    public List<Grade> findAllWithStudentName() {
        List<Grade> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT g.id, g.student_id, u.name AS student_name, g.course_name, g.score, g.semester " +
                    "FROM grades g INNER JOIN users u ON g.student_id = u.username " +
                    "ORDER BY g.id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Grade grade = new Grade();
                grade.setId(rs.getInt("id"));
                grade.setStudentId(rs.getString("student_id"));
                grade.setStudentName(rs.getString("student_name"));  // 设置学生姓名
                grade.setCourseName(rs.getString("course_name"));
                grade.setScore(rs.getDouble("score"));
                grade.setSemester(rs.getString("semester"));
                list.add(grade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 添加一条成绩记录
     * @param grade 要添加的成绩对象（id可忽略，自动生成）
     * @return 受影响的行数
     */
    public int add(Grade grade) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int rows = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO grades (student_id, course_name, score, semester) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, grade.getStudentId());
            pstmt.setString(2, grade.getCourseName());
            pstmt.setDouble(3, grade.getScore());
            pstmt.setString(4, grade.getSemester());
            rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return rows;
    }

    /**
     * 修改成绩分数
     * @param id 成绩记录的ID
     * @param newScore 新的分数
     * @return 受影响的行数
     */
    public int updateScore(int id, double newScore) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int rows = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE grades SET score = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, newScore);
            pstmt.setInt(2, id);
            rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return rows;
    }

    /**
     * 删除一条成绩记录
     * @param id 成绩记录的ID
     * @return 受影响的行数
     */
    public int delete(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int rows = 0;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM grades WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeConnection(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return rows;
    }
}