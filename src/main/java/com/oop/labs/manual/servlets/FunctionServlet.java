package com.oop.labs.manual.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oop.labs.manual.auth.AuthUtil;
import com.oop.labs.manual.dao.FunctionDao;
import com.oop.labs.manual.dto.Function;
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

@WebServlet("/functions/")
public class FunctionServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FunctionServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.info("GET request received for functions - parameters: id={}, name={}, author_id={}, sort={}, reverse={}",
                request.getParameter("id"), request.getParameter("name"),
                request.getParameter("author_id"), request.getParameter("sort"),
                request.getParameter("reverse"));

        // 🔐 ДОБАВЛЕНО: Аутентификация - все операции с функциями требуют авторизации
        User currentUser = AuthUtil.authenticate(request, response);
        if (currentUser == null) return;

        // 🔐 ДОБАВЛЕНО: Авторизация - только USER и ADMIN могут работать с функциями
        if (!AuthUtil.checkAuthorization(currentUser, "USER")) {
            AuthUtil.sendForbidden(response, "Insufficient permissions. USER role required");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection connection = DatabaseConnection.getConnection()) {
            FunctionDao functionDao = new FunctionDao(connection);

            String idParam = request.getParameter("id");
            String nameParam = request.getParameter("name");
            String authorIdParam = request.getParameter("author_id");
            String sortParam = request.getParameter("sort");
            String reverseParam = request.getParameter("reverse");

            if (idParam != null) {
                handleFindById(functionDao, idParam, currentUser, response, out); // 🔐 Передаем currentUser
            } else if (nameParam != null) {
                handleFindByName(functionDao, nameParam, currentUser, response, out); // 🔐 Передаем currentUser
            } else if (authorIdParam != null) {
                handleFindByAuthorId(functionDao, authorIdParam, sortParam, reverseParam, currentUser, response, out); // 🔐 Передаем currentUser
            } else {
                handleFindAll(functionDao, sortParam, reverseParam, currentUser, response, out); // 🔐 Передаем currentUser
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
        logger.info("POST request received for creating new function");

        User currentUser = AuthUtil.authenticate(request, response);
        if (currentUser == null) return;

        if (!AuthUtil.checkAuthorization(currentUser, "USER")) {
            AuthUtil.sendForbidden(response, "Insufficient permissions. USER role required");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection connection = DatabaseConnection.getConnection()) {
            FunctionDao functionDao = new FunctionDao(connection);

            Function function = objectMapper.readValue(request.getReader(), Function.class);
            logger.debug("Creating function: {}", function.getName());

            function.setAuthorId(currentUser.getId());

            int generatedId = functionDao.create(function);
            logger.info("Function created successfully with generated ID: {}", generatedId);

            Optional<Function> createdFunction = functionDao.findById(generatedId);

            if (createdFunction.isPresent()) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(objectMapper.writeValueAsString(createdFunction.get()));
            } else {
                logger.error("Failed to retrieve created function with ID: {}", generatedId);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to create function\"}");
            }
        } catch (Exception e) {
            logger.error("Error creating function", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid function data: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.info("PUT request received for updating function");

        // 🔐 ДОБАВЛЕНО: Аутентификация и авторизация
        User currentUser = AuthUtil.authenticate(request, response);
        if (currentUser == null) return;

        if (!AuthUtil.checkAuthorization(currentUser, "USER")) {
            AuthUtil.sendForbidden(response, "Insufficient permissions. USER role required");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection connection = DatabaseConnection.getConnection()) {
            FunctionDao functionDao = new FunctionDao(connection);

            Function updatedFunction = objectMapper.readValue(request.getReader(), Function.class);
            logger.debug("Updating function with ID: {}", updatedFunction.getId());

            Optional<Function> existingFunction = functionDao.findById(updatedFunction.getId());
            if (existingFunction.isPresent()) {
                if (existingFunction.get().getAuthorId() != currentUser.getId() &&
                        !AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
                    AuthUtil.sendForbidden(response, "You can only update your own functions");
                    return;
                }
            }

            boolean updated = functionDao.update(updatedFunction);

            if (updated) {
                logger.info("Function updated successfully with ID: {}", updatedFunction.getId());
                Optional<Function> function = functionDao.findById(updatedFunction.getId());
                if (function.isPresent()) {
                    out.print(objectMapper.writeValueAsString(function.get()));
                } else {
                    logger.error("Failed to retrieve updated function with ID: {}", updatedFunction.getId());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"Failed to retrieve updated function\"}");
                }
            } else {
                logger.warn("Function not found for update with ID: {}", updatedFunction.getId());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Function not found\"}");
            }
        } catch (Exception e) {
            logger.error("Error updating function", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid function data: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idParam = request.getParameter("id");
        logger.info("DELETE request received for function with ID: {}", idParam);

        // 🔐 ДОБАВЛЕНО: Аутентификация и авторизация
        User currentUser = AuthUtil.authenticate(request, response);
        if (currentUser == null) return;

        if (!AuthUtil.checkAuthorization(currentUser, "USER")) {
            AuthUtil.sendForbidden(response, "Insufficient permissions. USER role required");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (idParam != null) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                FunctionDao functionDao = new FunctionDao(connection);

                long id = Long.parseLong(idParam);
                logger.debug("Attempting to delete function with ID: {}", id);

                Optional<Function> function = functionDao.findById(id);
                if (function.isPresent()) {
                    if (function.get().getAuthorId() != currentUser.getId() &&
                            !AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
                        AuthUtil.sendForbidden(response, "You can only delete your own functions");
                        return;
                    }
                }

                boolean deleted = functionDao.delete(id);

                if (deleted) {
                    logger.info("Function deleted successfully with ID: {}", id);
                    out.print("{\"message\": \"Function deleted successfully\"}");
                } else {
                    logger.warn("Function not found for deletion with ID: {}", id);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Function not found\"}");
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

    // 🔐 ОБНОВЛЕНО: Все методы теперь принимают currentUser для проверки прав
    private void handleFindById(FunctionDao functionDao, String idParam, User currentUser,
                                HttpServletResponse response, PrintWriter out) throws SQLException {
        try {
            long id = Long.parseLong(idParam);
            logger.debug("Searching for function with ID: {}", id);

            Optional<Function> function = functionDao.findById(id);

            if (function.isPresent()) {
                if (function.get().getAuthorId() != currentUser.getId() &&
                        !AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
                    AuthUtil.sendForbidden(response, "Access denied to this function");
                    return;
                }

                logger.info("Function found with ID: {}", id);
                out.print(objectMapper.writeValueAsString(function.get()));
            } else {
                logger.warn("Function not found with ID: {}", id);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Function not found\"}");
            }
        } catch (NumberFormatException | JsonProcessingException e) {
            logger.error("Invalid ID format: {}", idParam, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid ID format\"}");
        }
    }

    private void handleFindByName(FunctionDao functionDao, String name, User currentUser,
                                  HttpServletResponse response, PrintWriter out) throws SQLException, JsonProcessingException {
        logger.debug("Searching for function with name: {}", name);

        Optional<Function> function = functionDao.findByName(name);

        if (function.isPresent()) {
            logger.info("Function found with name: {}", name);
            out.print(objectMapper.writeValueAsString(function.get()));
        } else {
            logger.warn("Function not found with name: {}", name);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\": \"Function not found\"}");
        }
    }

    private void handleFindByAuthorId(FunctionDao functionDao, String authorIdParam,
                                      String sortParam, String reverseParam, User currentUser,
                                      HttpServletResponse response, PrintWriter out) throws SQLException {
        try {
            long authorId = Long.parseLong(authorIdParam);
            List<Function> functions;

            if (!AuthUtil.checkAuthorization(currentUser, "ADMIN") && currentUser.getId() != authorId) {
                AuthUtil.sendForbidden(response, "You can only view your own functions");
                return;
            }

            logger.debug("Searching for functions by author ID: {}, sort: {}, reverse: {}",
                    authorId, sortParam, reverseParam);

            if (sortParam != null) {
                boolean isReversed = "true".equalsIgnoreCase(reverseParam);
                functions = functionDao.findByAuthorIdOrderedBy(authorId, sortParam, isReversed);
            } else {
                functions = functionDao.findByAuthorId(authorId);
            }

            logger.info("Found {} functions for author ID: {}", functions.size(), authorId);
            out.print(objectMapper.writeValueAsString(functions));
        } catch (NumberFormatException e) {
            logger.error("Invalid author_id format: {}", authorIdParam, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid author_id format\"}");
        } catch (JsonProcessingException e) {
            logger.error("JSON processing error", e);
            throw new RuntimeException(e);
        }
    }

    private void handleFindAll(FunctionDao functionDao, String sortParam, String reverseParam,
                               User currentUser, HttpServletResponse response, PrintWriter out)
            throws SQLException, JsonProcessingException {
        List<Function> functions;

        logger.debug("Retrieving all functions, sort: {}, reverse: {}", sortParam, reverseParam);

        if (AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
            if (sortParam != null) {
                boolean isReversed = "true".equalsIgnoreCase(reverseParam);
                functions = functionDao.findAllOrderedBy(sortParam, isReversed);
            } else {
                functions = functionDao.findAll();
            }
        } else {
            if (sortParam != null) {
                boolean isReversed = "true".equalsIgnoreCase(reverseParam);
                functions = functionDao.findByAuthorIdOrderedBy(currentUser.getId(), sortParam, isReversed);
            } else {
                functions = functionDao.findByAuthorId(currentUser.getId());
            }
        }

        logger.info("Retrieved {} functions", functions.size());
        out.print(objectMapper.writeValueAsString(functions));
    }
}