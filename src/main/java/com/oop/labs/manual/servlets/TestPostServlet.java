package com.oop.labs.manual.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/test-post")
public class TestPostServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        // Просто возвращаем полученные данные
        String body = request.getReader().lines().collect(Collectors.joining());
        response.getWriter().println("{\"received\": " + body + "}");
    }
}