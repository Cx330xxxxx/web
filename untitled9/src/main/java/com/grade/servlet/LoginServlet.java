package com.grade.servlet;

import com.grade.dao.UserDao;
import com.grade.model.User;
import com.grade.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if (username == null || password == null || role == null ||
                username.trim().isEmpty() || password.trim().isEmpty() || role.trim().isEmpty()) {
            out.print(JsonUtil.error("账号、密码和角色不能为空"));
            return;
        }

        User user = userDao.login(username.trim(), password.trim(), role.trim());

        if (user != null) {
            request.getSession().setAttribute("currentUser", user);
            String userJson = "{" +
                    "\"username\": \"" + JsonUtil.escapeJson(user.getUsername()) + "\"," +
                    "\"name\": \"" + JsonUtil.escapeJson(user.getName()) + "\"," +
                    "\"role\": \"" + JsonUtil.escapeJson(user.getRole()) + "\"" +
                    "}";
            out.print(JsonUtil.success("{\"user\": " + userJson + "}"));
        } else {
            out.print(JsonUtil.error("账号或密码错误"));
        }
    }
}