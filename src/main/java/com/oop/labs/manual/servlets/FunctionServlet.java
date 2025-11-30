package com.oop.labs.manual.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oop.labs.manual.dao.FunctionDao;
import com.oop.labs.manual.dto.Function;
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
                handleFindById(functionDao, idParam, response, out);
            } else if (nameParam != null) {
                handleFindByName(functionDao, nameParam, response, out);
            } else if (authorIdParam != null) {
                handleFindByAuthorId(functionDao, authorIdParam, sortParam, reverseParam, response, out);
            } else {
                handleFindAll(functionDao, sortParam, reverseParam, response, out);
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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection connection = DatabaseConnection.getConnection()) {
            FunctionDao functionDao = new FunctionDao(connection);

            Function function = objectMapper.readValue(request.getReader(), Function.class);
            logger.debug("Creating function: {}", function.getName());

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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection connection = DatabaseConnection.getConnection()) {
            FunctionDao functionDao = new FunctionDao(connection);

            Function updatedFunction = objectMapper.readValue(request.getReader(), Function.class);
            logger.debug("Updating function with ID: {}", updatedFunction.getId());

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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (idParam != null) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                FunctionDao functionDao = new FunctionDao(connection);

                long id = Long.parseLong(idParam);
                logger.debug("Attempting to delete function with ID: {}", id);

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

    private void handleFindById(FunctionDao functionDao, String idParam,
                                HttpServletResponse response, PrintWriter out) throws SQLException {
        try {
            long id = Long.parseLong(idParam);
            logger.debug("Searching for function with ID: {}", id);

            Optional<Function> function = functionDao.findById(id);

            if (function.isPresent()) {
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

    private void handleFindByName(FunctionDao functionDao, String name,
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
                                      String sortParam, String reverseParam,
                                      HttpServletResponse response, PrintWriter out) throws SQLException {
        try {
            long authorId = Long.parseLong(authorIdParam);
            List<Function> functions;

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
                               HttpServletResponse response, PrintWriter out) throws SQLException, JsonProcessingException {
        List<Function> functions;

        logger.debug("Retrieving all functions, sort: {}, reverse: {}", sortParam, reverseParam);

        if (sortParam != null) {
            boolean isReversed = "true".equalsIgnoreCase(reverseParam);
            functions = functionDao.findAllOrderedBy(sortParam, isReversed);
        } else {
            functions = functionDao.findAll();
        }

        logger.info("Retrieved {} functions", functions.size());
        out.print(objectMapper.writeValueAsString(functions));
    }
}