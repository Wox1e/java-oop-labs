package com.oop.labs.manual.auth;

import com.oop.labs.manual.dao.UserDao;
import com.oop.labs.manual.dto.User;
import com.oop.labs.manual.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Base64;
import java.util.Optional;

public class AuthUtil {
    private static final Logger logger = LoggerFactory.getLogger(AuthUtil.class);

    public static User authenticate(HttpServletRequest request, HttpServletResponse response) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            sendUnauthorized(response, "Missing Authorization header");
            return null;
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length());
            String credentials = new String(
                    Base64.getDecoder().decode(base64Credentials),
                    StandardCharsets.UTF_8
            );
            String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                sendUnauthorized(response, "Invalid credential format");
                return null;
            }

            String username = values[0];
            String password = values[1];

            try (Connection connection = DatabaseConnection.getConnection()) {
                UserDao userDao = new UserDao(connection);
                Optional<User> userOpt = userDao.findByUsername(username);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (user.getPassword_hash().equals(password)) {
                        logger.info("User authenticated: {}", username);
                        return user;
                    }
                }
            }

            logger.warn("Authentication failed for user: {}", username);
            sendUnauthorized(response, "Invalid credentials");
            return null;

        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage());
            sendUnauthorized(response, "Authentication failed");
            return null;
        }
    }


    public static boolean checkAuthorization(User user, String requiredRole) {
        return user.getRoles().contains(requiredRole) || user.getRoles().contains("ADMIN");
    }

    public static void sendUnauthorized(HttpServletResponse response, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("WWW-Authenticate", "Basic realm=\"OOP API\"");
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + message + "\"}");
        } catch (IOException e) {
            logger.error("Error sending unauthorized response: {}", e.getMessage());
        }
    }

    public static void sendForbidden(HttpServletResponse response, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + message + "\"}");
        } catch (IOException e) {
            logger.error("Error sending forbidden response: {}", e.getMessage());
        }
    }
}