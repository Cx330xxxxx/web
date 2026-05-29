package com.grade.servlet;

import com.grade.dao.GradeDao;
import com.grade.model.Grade;
import com.grade.model.User;
import com.grade.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/student/grades")
public class StudentGradeServlet extends HttpServlet {

    private GradeDao gradeDao = new GradeDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            out.print(JsonUtil.error("请先登录"));
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if (!"student".equals(currentUser.getRole())) {
            out.print(JsonUtil.error("权限不足"));
            return;
        }

        String studentId = currentUser.getUsername();
        List<Grade> gradeList = gradeDao.findByStudentId(studentId);

        // 构建成绩数组 JSON
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < gradeList.size(); i++) {
            Grade g = gradeList.get(i);
            sb.append("{");
            sb.append("\"courseName\": \"").append(JsonUtil.escapeJson(g.getCourseName())).append("\",");
            sb.append("\"score\": ").append(g.getScore()).append(",");
            sb.append("\"semester\": \"").append(JsonUtil.escapeJson(g.getSemester() != null ? g.getSemester() : "")).append("\"");
            sb.append("}");
            if (i < gradeList.size() - 1) sb.append(",");
        }
        sb.append("]");

        out.print(JsonUtil.success(sb.toString()));
    }
}