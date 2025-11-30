package com.oop.labs.manual.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oop.labs.manual.dao.UserDao;
import com.oop.labs.manual.dto.User;
import com.oop.labs.manual.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@WebServlet("/users/")
public class UserServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
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
            throws IOException {
        logger.info("=== POST REQUEST START ===");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection connection = DatabaseConnection.getConnection()) {
            UserDao userDao = new UserDao(connection);

            String rawJson = request.getReader().lines().collect(Collectors.joining());
            logger.debug("Raw JSON: {}", rawJson);

            User user = objectMapper.readValue(rawJson, User.class);
            logger.info("Parsed user - username: {}", user.getUsername());

            logger.info("Attempting to create user in database...");
            long generatedId = userDao.create(user);
            logger.info("User created with generated ID: {}", generatedId);

            logger.info("Verifying user in database...");
            Optional<User> createdUser = userDao.findById(generatedId);

            if (createdUser.isPresent()) {
                logger.info("SUCCESS: User found in DB - ID: {}, username: {}",
                        createdUser.get().getId(), createdUser.get().getUsername());

                response.setStatus(HttpServletResponse.SC_OK);
                out.print(objectMapper.writeValueAsString(createdUser.get()));
            } else {
                logger.error("FAILED: User not found in DB after creation! ID: {}", generatedId);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to create user\"}");
            }

        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid user data: " + e.getMessage() + "\"}");
        }

        logger.info("=== POST REQUEST END ===");
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
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
            throws IOException {
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