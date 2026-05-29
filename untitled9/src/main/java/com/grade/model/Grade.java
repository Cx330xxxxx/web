package com.grade.model;

public class Grade {
    private int id;
    private String studentId;      // 学号，对应users表的username
    private String studentName;    // 学生姓名（从users表关联查询）
    private String courseName;     // 课程名称
    private double score;          // 成绩
    private String semester;       // 学期，如 "2024-2025-1"

    // 默认构造方法
    public Grade() {}

    // 不含id的构造方法（用于新增）
    public Grade(String studentId, String courseName, double score, String semester) {
        this.studentId = studentId;
        this.courseName = courseName;
        this.score = score;
        this.semester = semester;
    }

    // 完整构造方法
    public Grade(int id, String studentId, String studentName, String courseName, double score, String semester) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseName = courseName;
        this.score = score;
        this.semester = semester;
    }

    // Getter 和 Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", courseName='" + courseName + '\'' +
                ", score=" + score +
                ", semester='" + semester + '\'' +
                '}';
    }
}