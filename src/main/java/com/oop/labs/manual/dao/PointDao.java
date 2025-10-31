package com.oop.labs.manual.dao;

import com.oop.labs.manual.pojo.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PointDao {
    private static final Logger logger = LoggerFactory.getLogger(PointDao.class);
    private final Connection connection;

    public PointDao(Connection connection) {
        this.connection = connection;
    }

    public long create(Point point) {
        String query = "INSERT INTO points (function_id, x_value, y_value) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, point.getFunctionId());
            stmt.setDouble(2, point.getXValue());
            stmt.setDouble(3, point.getYValue());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                logger.info("Создана точка с id: {}", id);
                return id;
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания точки", e);
            throw new RuntimeException("Ошибка создания точки", e);
        }
        return -1;
    }

    public Optional<Point> findById(long id) {
        String sql = "SELECT * FROM points WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Point point = resultSetToPoint(rs);
                logger.info("Найдена точка с id: {}", point.getId());
                return Optional.of(point);
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска точки с id: {}", id, e);
        }
        return Optional.empty();
    }

    public List<Point> findByFunctionId(int functionId) {
        List<Point> points = new ArrayList<>();
        String sql = "SELECT * FROM points WHERE function_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, functionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                points.add(resultSetToPoint(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска точек по function_id: {}", functionId, e);
        }
        return points;
    }

    public List<Point> findAll() {
        List<Point> points = new ArrayList<>();
        String sql = "SELECT * FROM points";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                points.add(resultSetToPoint(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all points", e);
        }
        return points;
    }

    public boolean update(Point point) {
        String sql = "UPDATE points SET function_id = ?, x_value = ?, y_value = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, point.getFunctionId());
            stmt.setDouble(2, point.getXValue());
            stmt.setDouble(3, point.getYValue());
            stmt.setLong(4, point.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Обновлена точка с id: {}", point.getId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка обновления точки с id: {}", point.getId(), e);
        }
        return false;
    }

    public boolean delete(long id) {
        String sql = "DELETE FROM points WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Удалена точка с id: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка удаления точки с id: {}", id, e);
        }
        return false;
    }

    public boolean deleteByFunctionId(int functionId) {
        String sql = "DELETE FROM points WHERE function_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, functionId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Удалены точки с function_id: {}", functionId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка удаления точек с function_id: {}", functionId, e);
        }
        return false;
    }

    private Point resultSetToPoint(ResultSet rs) {
        try {
            long id = rs.getLong("id");
            int functionId = rs.getInt("function_id");
            double xValue = rs.getDouble("x_value");
            double yValue = rs.getDouble("y_value");
            return new Point(id, functionId, xValue, yValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}