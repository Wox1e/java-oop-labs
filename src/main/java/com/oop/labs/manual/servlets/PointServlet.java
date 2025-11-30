package com.oop.labs.manual.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oop.labs.manual.dao.PointDao;
import com.oop.labs.manual.dto.Point;
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
                handleFindById(pointDao, idParam, response, out);
            } else if (functionIdParam != null) {
                handleFindByFunctionId(pointDao, functionIdParam, sortParam, reverseParam, response, out);
            } else {
                handleFindAll(pointDao, sortParam, reverseParam, response, out);
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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection connection = DatabaseConnection.getConnection()) {
            PointDao pointDao = new PointDao(connection);

            Point point = objectMapper.readValue(request.getReader(), Point.class);
            logger.debug("Creating point for function ID: {}", point.getFunctionId());

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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection connection = DatabaseConnection.getConnection()) {
            PointDao pointDao = new PointDao(connection);

            Point updatedPoint = objectMapper.readValue(request.getReader(), Point.class);
            logger.debug("Updating point with ID: {}", updatedPoint.getId());

            boolean updated = pointDao.update(updatedPoint);

            if (updated) {
                logger.info("Point updated successfully with ID: {}", updatedPoint.getId());
                Optional<Point> point = pointDao.findById(updatedPoint.getId());
                if (point.isPresent()) {
                    out.print(objectMapper.writeValueAsString(point.get()));
                } else {
                    logger.error("Failed to retrieve updated point with ID: {}", updatedPoint.getId());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"error\": \"Failed to retrieve updated point\"}");
                }
            } else {
                logger.warn("Point not found for update with ID: {}", updatedPoint.getId());
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Point not found\"}");
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
        String idParam = request.getParameter("id");
        String functionIdParam = request.getParameter("function_id");
        logger.info("DELETE request received for points - id: {}, function_id: {}", idParam, functionIdParam);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (idParam != null) {
            handleDeleteById(idParam, response, out);
        } else if (functionIdParam != null) {
            handleDeleteByFunctionId(functionIdParam, response, out);
        } else {
            logger.warn("DELETE request missing required ID or function_id parameter");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"ID or function_id parameter is required\"}");
        }
        out.flush();
    }

    private void handleFindById(PointDao pointDao, String idParam,
                                HttpServletResponse response, PrintWriter out) throws SQLException {
        try {
            long id = Long.parseLong(idParam);
            logger.debug("Searching for point with ID: {}", id);

            Optional<Point> point = pointDao.findById(id);

            if (point.isPresent()) {
                logger.info("Point found with ID: {}", id);
                out.print(objectMapper.writeValueAsString(point.get()));
            } else {
                logger.warn("Point not found with ID: {}", id);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Point not found\"}");
            }
        } catch (NumberFormatException | JsonProcessingException e) {
            logger.error("Invalid ID format: {}", idParam, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid ID format\"}");
        }
    }

    private void handleFindByFunctionId(PointDao pointDao, String functionIdParam,
                                        String sortParam, String reverseParam,
                                        HttpServletResponse response, PrintWriter out) throws SQLException {
        try {
            long functionId = Long.parseLong(functionIdParam);
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
                               HttpServletResponse response, PrintWriter out) throws SQLException, JsonProcessingException {
        List<Point> points;

        logger.debug("Retrieving all points, sort: {}, reverse: {}", sortParam, reverseParam);

        if (sortParam != null) {
            boolean isReversed = "true".equalsIgnoreCase(reverseParam);
            points = pointDao.findAllOrderedBy(sortParam, isReversed);
        } else {
            points = pointDao.findAll();
        }

        logger.info("Retrieved {} points", points.size());
        out.print(objectMapper.writeValueAsString(points));
    }

    private void handleDeleteById(String idParam, HttpServletResponse response, PrintWriter out) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            PointDao pointDao = new PointDao(connection);

            long id = Long.parseLong(idParam);
            logger.debug("Attempting to delete point with ID: {}", id);

            boolean deleted = pointDao.delete(id);

            if (deleted) {
                logger.info("Point deleted successfully with ID: {}", id);
                out.print("{\"message\": \"Point deleted successfully\"}");
            } else {
                logger.warn("Point not found for deletion with ID: {}", id);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Point not found\"}");
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
    }

    private void handleDeleteByFunctionId(String functionIdParam, HttpServletResponse response, PrintWriter out) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            PointDao pointDao = new PointDao(connection);

            long functionId = Long.parseLong(functionIdParam);
            logger.debug("Attempting to delete all points for function ID: {}", functionId);

            boolean deleted = pointDao.deleteByFunctionId(functionId);

            if (deleted) {
                logger.info("All points deleted successfully for function ID: {}", functionId);
                out.print("{\"message\": \"All points for function deleted successfully\"}");
            } else {
                logger.warn("No points found for deletion with function ID: {}", functionId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"No points found for this function\"}");
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid function_id format: {}", functionIdParam, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid function_id format\"}");
        } catch (SQLException e) {
            logger.error("Database error during DELETE operation", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        }
    }
}