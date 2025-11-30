package com.oop.labs.manual.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oop.labs.manual.auth.AuthUtil;
import com.oop.labs.manual.dao.FunctionDao;
import com.oop.labs.manual.dao.PointDao;
import com.oop.labs.manual.dto.Function;
import com.oop.labs.manual.dto.Point;
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

@WebServlet("/points/")
public class PointServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PointServlet.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.info("GET request received for points - parameters: id={}, function_id={}, sort={}, reverse={}",
                request.getParameter("id"), request.getParameter("function_id"),
                request.getParameter("sort"), request.getParameter("reverse"));

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
            PointDao pointDao = new PointDao(connection);

            String idParam = request.getParameter("id");
            String functionIdParam = request.getParameter("function_id");
            String sortParam = request.getParameter("sort");
            String reverseParam = request.getParameter("reverse");

            if (idParam != null) {
                handleFindById(pointDao, idParam, currentUser, response, out);
            } else if (functionIdParam != null) {
                handleFindByFunctionId(pointDao, functionIdParam, sortParam, reverseParam, currentUser, response, out);
            } else {
                handleFindAll(pointDao, sortParam, reverseParam, currentUser, response, out);
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
        logger.info("POST request received for creating new point");

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
            PointDao pointDao = new PointDao(connection);
            FunctionDao functionDao = new FunctionDao(connection);

            Point point = objectMapper.readValue(request.getReader(), Point.class);
            logger.debug("Creating point for function ID: {}", point.getFunctionId());

            // 🔐 ПРОВЕРКА: Проверка что пользователь имеет доступ к функции
            Optional<Function> function = functionDao.findById(point.getFunctionId());
            if (function.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Function not found\"}");
                return;
            }

            // Проверка прав доступа к функции
            if (function.get().getAuthorId() != currentUser.getId() &&
                    !AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
                AuthUtil.sendForbidden(response, "Access denied to this function");
                return;
            }

            long generatedId = pointDao.create(point);
            logger.info("Point created successfully with generated ID: {}", generatedId);

            Optional<Point> createdPoint = pointDao.findById(generatedId);

            if (createdPoint.isPresent()) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print(objectMapper.writeValueAsString(createdPoint.get()));
            } else {
                logger.error("Failed to retrieve created point with ID: {}", generatedId);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to create point\"}");
            }
        } catch (Exception e) {
            logger.error("Error creating point", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid point data: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.info("PUT request received for updating point");

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
            PointDao pointDao = new PointDao(connection);
            FunctionDao functionDao = new FunctionDao(connection);

            Point point = objectMapper.readValue(request.getReader(), Point.class);

            // Проверяем существование точки
            Optional<Point> existingPoint = pointDao.findById(point.getId());
            if (existingPoint.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Point not found\"}");
                return;
            }

            // 🔐 ПРОВЕРКА: Проверка прав доступа к функции точки
            Optional<Function> function = functionDao.findById(existingPoint.get().getFunctionId());
            if (function.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Function not found\"}");
                return;
            }

            if (function.get().getAuthorId() != currentUser.getId() &&
                    !AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
                AuthUtil.sendForbidden(response, "Access denied to this function");
                return;
            }

            boolean updated = pointDao.update(point);
            if (updated) {
                logger.info("Point updated successfully with ID: {}", point.getId());
                Optional<Point> updatedPoint = pointDao.findById(point.getId());
                out.print(objectMapper.writeValueAsString(updatedPoint.get()));
            } else {
                logger.error("Failed to update point with ID: {}", point.getId());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to update point\"}");
            }
        } catch (Exception e) {
            logger.error("Error updating point", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid point data: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.info("DELETE request received for point");

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
            PointDao pointDao = new PointDao(connection);
            FunctionDao functionDao = new FunctionDao(connection);

            String idParam = request.getParameter("id");
            String functionIdParam = request.getParameter("function_id");

            if (idParam != null) {
                handleDeleteById(pointDao, functionDao, idParam, currentUser, response, out);
            } else if (functionIdParam != null) {
                handleDeleteByFunctionId(pointDao, functionDao, functionIdParam, currentUser, response, out);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Missing id or function_id parameter\"}");
            }
        } catch (SQLException e) {
            logger.error("Database error during DELETE operation", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        }
        out.flush();
    }

    private void handleFindById(PointDao pointDao, String idParam, User currentUser,
                                HttpServletResponse response, PrintWriter out) throws SQLException {
        try {
            long id = Long.parseLong(idParam);
            Optional<Point> point = pointDao.findById(id);

            if (point.isPresent()) {
                if (!AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
                    try (Connection connection = DatabaseConnection.getConnection()) {
                        FunctionDao functionDao = new FunctionDao(connection);
                        Optional<Function> function = functionDao.findById(point.get().getFunctionId());

                        if (function.isEmpty()) {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.print("{\"error\": \"Function not found\"}");
                            return;
                        }

                        // Проверяем что пользователь является автором функции
                        if (function.get().getAuthorId() != currentUser.getId()) {
                            AuthUtil.sendForbidden(response, "Access denied to this point");
                            return;
                        }
                    }
                }

                out.print(objectMapper.writeValueAsString(point.get()));
                logger.info("Point found with ID: {}", id);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Point not found\"}");
            }
        } catch (NumberFormatException | JsonProcessingException e) {
            logger.error("Invalid id format: {}", idParam, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid id format\"}");
        }
    }

    private void handleFindByFunctionId(PointDao pointDao, String functionIdParam,
                                        String sortParam, String reverseParam, User currentUser,
                                        HttpServletResponse response, PrintWriter out) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            FunctionDao functionDao = new FunctionDao(connection);
            long functionId = Long.parseLong(functionIdParam);

            Optional<Function> function = functionDao.findById(functionId);
            if (function.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Function not found\"}");
                return;
            }

            if (function.get().getAuthorId() != currentUser.getId() &&
                    !AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
                AuthUtil.sendForbidden(response, "Access denied to this function");
                return;
            }

            List<Point> points;
            logger.debug("Searching for points by function ID: {}, sort: {}, reverse: {}",
                    functionId, sortParam, reverseParam);

            if (sortParam != null) {
                boolean isReversed = "true".equalsIgnoreCase(reverseParam);
                points = pointDao.findByFunctionIdOrderedBy(functionId, sortParam, isReversed);
            } else {
                points = pointDao.findByFunctionId(functionId);
            }

            logger.info("Found {} points for function ID: {}", points.size(), functionId);
            out.print(objectMapper.writeValueAsString(points));
        } catch (NumberFormatException | JsonProcessingException e) {
            logger.error("Invalid function_id format: {}", functionIdParam, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid function_id format\"}");
        }
    }

    private void handleFindAll(PointDao pointDao, String sortParam, String reverseParam,
                               User currentUser, HttpServletResponse response, PrintWriter out)
            throws SQLException, JsonProcessingException {
        List<Point> points;

        logger.debug("Retrieving all points, sort: {}, reverse: {}", sortParam, reverseParam);

        if (AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
            if (sortParam != null) {
                boolean isReversed = "true".equalsIgnoreCase(reverseParam);
                points = pointDao.findAllOrderedBy(sortParam, isReversed);
            } else {
                points = pointDao.findAll();
            }
        } else {
            if (sortParam != null) {
                boolean isReversed = "true".equalsIgnoreCase(reverseParam);
                points = pointDao.findByUserIdOrderedBy(currentUser.getId(), sortParam, isReversed);
            } else {
                points = pointDao.findByUserId(currentUser.getId());
            }
        }

        logger.info("Retrieved {} points", points.size());
        out.print(objectMapper.writeValueAsString(points));
    }

    private void handleDeleteById(PointDao pointDao, FunctionDao functionDao, String idParam,
                                  User currentUser, HttpServletResponse response, PrintWriter out)
            throws SQLException {
        try {
            long id = Long.parseLong(idParam);

            Optional<Point> point = pointDao.findById(id);
            if (point.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Point not found\"}");
                return;
            }

            Optional<Function> function = functionDao.findById(point.get().getFunctionId());
            if (function.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Function not found\"}");
                return;
            }

            if (function.get().getAuthorId() != currentUser.getId() &&
                    !AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
                AuthUtil.sendForbidden(response, "Access denied to this function");
                return;
            }

            boolean deleted = pointDao.delete(id);
            if (deleted) {
                logger.info("Point deleted successfully with ID: {}", id);
                out.print("{\"message\": \"Point deleted successfully\"}");
            } else {
                logger.error("Failed to delete point with ID: {}", id);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to delete point\"}");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid id format: {}", idParam, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid id format\"}");
        }
    }

    private void handleDeleteByFunctionId(PointDao pointDao, FunctionDao functionDao, String functionIdParam,
                                          User currentUser, HttpServletResponse response, PrintWriter out)
            throws SQLException {
        try {
            long functionId = Long.parseLong(functionIdParam);

            Optional<Function> function = functionDao.findById(functionId);
            if (function.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Function not found\"}");
                return;
            }

            if (function.get().getAuthorId() != currentUser.getId() &&
                    !AuthUtil.checkAuthorization(currentUser, "ADMIN")) {
                AuthUtil.sendForbidden(response, "Access denied to this function");
                return;
            }

            boolean deleted = pointDao.deleteByFunctionId(functionId);
            if (deleted) {
                logger.info("All points deleted successfully for function ID: {}", functionId);
                out.print("{\"message\": \"All points for function deleted successfully\"}");
            } else {
                logger.error("Failed to delete points for function ID: {}", functionId);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to delete points for function\"}");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid function_id format: {}", functionIdParam, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid function_id format\"}");
        }
    }
}