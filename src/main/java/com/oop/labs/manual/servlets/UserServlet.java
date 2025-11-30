package com.oop.labs.manual.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oop.labs.manual.dao.UserDao;
import com.oop.labs.manual.dto.User;
import com.oop.labs.manual.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("GET request received for users - ID parameter: {}", request.getParameter("id"));

        PrintWriter out = response.getWriter();

        try (Connection connection = DatabaseConnection.getConnection()) {
            UserDao userDao = new UserDao(connection);
            String idParam = request.getParameter("id");

            if (idParam != null) {
                try {
                    long id = Long.parseLong(idParam);
                    logger.debug("Searching for user with ID: {}", id);

                    Optional<User> user = userDao.findById(id);

                    if (user.isPresent()) {
                        logger.info("User found with ID: {}", id);
                        out.print(objectMapper.writeValueAsString(user.get()));
                    } else {
                        logger.warn("User not found with ID: {}", id);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"error\": \"User not found\"}");
                    }
                } catch (NumberFormatException e) {
                    logger.error("Invalid ID format: {}", idParam, e);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Invalid ID format\"}");
                }
            } else {
                logger.debug("Retrieving all users");
                List<User> users = userDao.findAll();
                logger.info("Retrieved {} users", users.size());
                out.print(objectMapper.writeValueAsString(users));
            }
        } catch (SQLException e) {
            logger.error("Database error during GET operation", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ВРЕМЕННАЯ ДИАГНОСТИКА
        System.out.println("=== POST REQUEST RECEIVED ===");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Получаем сырой JSON
            String rawJson = request.getReader().lines().collect(Collectors.joining());
            System.out.println("Raw JSON: " + rawJson);

            // Пробуем распарсить
            System.out.println("Attempting to parse JSON...");
            User user = objectMapper.readValue(rawJson, User.class);
            System.out.println("Success! Username: " + user.getUsername());

            out.print("{\"status\": \"success\", \"username\": \"" + user.getUsername() + "\"}");

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"JSON parse error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("PUT request received for updating user");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection connection = DatabaseConnection.getConnection()) {
            UserDao userDao = new UserDao(connection);

            User updatedUser = objectMapper.readValue(request.getReader(), User.class);
            logger.debug("Updating user with ID: {}", updatedUser.getId());

            boolean updated = userDao.update(updatedUser);

            if (updated) {
                logger.info("User updated successfully with ID: {}", updatedUser.getId());
                Optional<User> user = userDao.findById(updatedUser.getId());
                if (user.isPresent()) {
                    out.print(objectMapper.writeValueAsString(user.get()));
                } else {
                    logger.error("Failed to retrieve updated user with ID: {}", updatedUser.getId());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"Failed to retrieve updated user\"}");
                }
            } else {
                logger.warn("User not found for update with ID: {}", updatedUser.getId());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"User not found\"}");
            }
        } catch (Exception e) {
            logger.error("Error updating user", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid user data: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        logger.info("DELETE request received for user with ID: {}", idParam);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (idParam != null) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                UserDao userDao = new UserDao(connection);

                long id = Long.parseLong(idParam);
                logger.debug("Attempting to delete user with ID: {}", id);

                boolean deleted = userDao.delete(id);

                if (deleted) {
                    logger.info("User deleted successfully with ID: {}", id);
                    out.print("{\"message\": \"User deleted successfully\"}");
                } else {
                    logger.warn("User not found for deletion with ID: {}", id);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"User not found\"}");
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid ID format: {}", idParam, e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid ID format\"}");
            } catch (SQLException e) {
                logger.error("Database error during DELETE operation", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
            }
        } else {
            logger.warn("DELETE request missing required ID parameter");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"ID parameter is required\"}");
        }
        out.flush();
    }
}