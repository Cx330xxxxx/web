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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/teacher/grades/*")
public class TeacherGradeServlet extends HttpServlet {

    private GradeDao gradeDao = new GradeDao();

    private boolean isTeacher(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) return false;
        User user = (User) session.getAttribute("currentUser");
        return "teacher".equals(user.getRole());
    }

    private String extractIdFromPath(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) return null;
        return pathInfo.substring(1);
    }

    private String readJsonBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        int start = colonIndex + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        if (start >= json.length()) return null;
        if (json.charAt(start) == '"') {
            int end = json.indexOf("\"", start + 1);
            if (end == -1) return null;
            return json.substring(start + 1, end);
        } else {
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
        }
    }

    private void handleGetAll(HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        List<Grade> gradeList = gradeDao.findAllWithStudentName();

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < gradeList.size(); i++) {
            Grade g = gradeList.get(i);
            sb.append("{");
            sb.append("\"id\": ").append(g.getId()).append(",");
            sb.append("\"studentId\": \"").append(JsonUtil.escapeJson(g.getStudentId())).append("\",");
            sb.append("\"studentName\": \"").append(JsonUtil.escapeJson(g.getStudentName() != null ? g.getStudentName() : "")).append("\",");
            sb.append("\"courseName\": \"").append(JsonUtil.escapeJson(g.getCourseName())).append("\",");
            sb.append("\"score\": ").append(g.getScore());
            if (g.getSemester() != null) {
                sb.append(", \"semester\": \"").append(JsonUtil.escapeJson(g.getSemester())).append("\"");
            }
            sb.append("}");
            if (i < gradeList.size() - 1) sb.append(",");
        }
        sb.append("]");

        out.print(JsonUtil.success(sb.toString()));
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String jsonBody = readJsonBody(request);
        if (jsonBody == null || jsonBody.isEmpty()) {
            out.print(JsonUtil.error("请求数据为空"));
            return;
        }

        String studentId = extractJsonValue(jsonBody, "studentId");
        String courseName = extractJsonValue(jsonBody, "courseName");
        String scoreStr = extractJsonValue(jsonBody, "score");

        if (studentId == null || courseName == null || scoreStr == null ||
                studentId.isEmpty() || courseName.isEmpty() || scoreStr.isEmpty()) {
            out.print(JsonUtil.error("学号、课程名称和成绩不能为空"));
            return;
        }

        double score;
        try {
            score = Double.parseDouble(scoreStr);
            if (score < 0 || score > 100) {
                out.print(JsonUtil.error("成绩必须在0-100之间"));
                return;
            }
        } catch (NumberFormatException e) {
            out.print(JsonUtil.error("成绩格式不正确"));
            return;
        }

        Grade grade = new Grade(studentId, courseName, score, null);
        int rows = gradeDao.add(grade);
        if (rows > 0) {
            out.print(JsonUtil.success());
        } else {
            out.print(JsonUtil.error("添加失败"));
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response, String gradeId) throws IOException {
        PrintWriter out = response.getWriter();
        if (gradeId == null) {
            out.print(JsonUtil.error("缺少成绩ID"));
            return;
        }

        int id;
        try {
            id = Integer.parseInt(gradeId);
        } catch (NumberFormatException e) {
            out.print(JsonUtil.error("无效的成绩ID"));
            return;
        }

        String jsonBody = readJsonBody(request);
        String scoreStr = extractJsonValue(jsonBody, "score");
        if (scoreStr == null || scoreStr.isEmpty()) {
            out.print(JsonUtil.error("缺少新的成绩"));
            return;
        }

        double score;
        try {
            score = Double.parseDouble(scoreStr);
            if (score < 0 || score > 100) {
                out.print(JsonUtil.error("成绩必须在0-100之间"));
                return;
            }
        } catch (NumberFormatException e) {
            out.print(JsonUtil.error("成绩格式不正确"));
            return;
        }

        int rows = gradeDao.updateScore(id, score);
        if (rows > 0) {
            out.print(JsonUtil.success());
        } else {
            out.print(JsonUtil.error("修改失败，可能记录不存在"));
        }
    }

    private void handleDelete(HttpServletResponse response, String gradeId) throws IOException {
        PrintWriter out = response.getWriter();
        if (gradeId == null) {
            out.print(JsonUtil.error("缺少成绩ID"));
            return;
        }

        int id;
        try {
            id = Integer.parseInt(gradeId);
        } catch (NumberFormatException e) {
            out.print(JsonUtil.error("无效的成绩ID"));
            return;
        }

        int rows = gradeDao.delete(id);
        if (rows > 0) {
            out.print(JsonUtil.success());
        } else {
            out.print(JsonUtil.error("删除失败，可能记录不存在"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        if (!isTeacher(request)) {
            response.getWriter().print(JsonUtil.error("请先以教师身份登录"));
            return;
        }
        handleGetAll(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        if (!isTeacher(request)) {
            response.getWriter().print(JsonUtil.error("请先以教师身份登录"));
            return;
        }
        handleAdd(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        if (!isTeacher(request)) {
            response.getWriter().print(JsonUtil.error("请先以教师身份登录"));
            return;
        }
        String gradeId = extractIdFromPath(request.getPathInfo());
        handleUpdate(request, response, gradeId);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        if (!isTeacher(request)) {
            response.getWriter().print(JsonUtil.error("请先以教师身份登录"));
            return;
        }
        String gradeId = extractIdFromPath(request.getPathInfo());
        handleDelete(response, gradeId);
    }
}