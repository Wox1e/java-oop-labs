package dao;

import entities.PointEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class PointDAO implements PointDAOInterface{
    private static final Logger logger = LoggerFactory.getLogger(PointDAO.class);
    private final Connection connection;

    public PointDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UUID create(PointEntity point) {
        String query = "INSERT INTO points (function_id, x_value, y_value) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, point.getFunction_id());
            stmt.setDouble(2, point.getX_value());
            stmt.setDouble(3, point.getY_value());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                logger.info("Создана точка с id: {}", id);
                return id;
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания точки", e);
            throw new RuntimeException("Ошибка создания точки", e);
        }
        return null;
    }

    @Override
    public Optional<PointEntity> findById(UUID id) {
        String sql = "SELECT * FROM points WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PointEntity point = resultSetToPointEntity(rs);
                logger.info("Найдена точка с id: {}", point.getId());
                return Optional.of(point);
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска точки с id: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<PointEntity> findByFunctionId(UUID functionId) {
        List<PointEntity> points = new ArrayList<>();
        String sql = "SELECT * FROM points WHERE function_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, functionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                points.add(resultSetToPointEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска точек по function_id: {}", functionId, e);
        }
        return points;
    }

    @Override
    public List<PointEntity> findAll() {
        List<PointEntity> points = new ArrayList<>();
        String sql = "SELECT * FROM points";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                points.add(resultSetToPointEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all points", e);
        }
        return points;
    }

    @Override
    public List<PointEntity> findAllOrderedBy(String field, boolean isReversed) {
        String direction = isReversed ? "DESC" : "ASC";
        logger.info("Поиск всех точек с сортировкой по полю: {} направление: {}", field, direction);
        List<PointEntity> points = new ArrayList<>();

        Set<String> allowed = Set.of("x_value", "y_value", "function_id", "id");
        if (!allowed.contains(field)) throw new IllegalArgumentException();

        String sql = "SELECT * FROM points ORDER BY " + field + " " + direction;

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                points.add(resultSetToPointEntity(rs));
            }
            logger.info("Найдено точек с сортировкой: {}", points.size());
        } catch (SQLException e) {
            logger.error("Ошибка поиска всех точек с сортировкой", e);
        }
        return points;
    }

    @Override
    public List<PointEntity> findByFunctionIdOrderedBy(UUID functionId, String field, boolean isReversed) {
        String direction = isReversed ? "DESC" : "ASC";
        logger.info("Поиск точек по function_id: {} с сортировкой по полю: {} направление: {}", functionId, field, direction);
        List<PointEntity> points = new ArrayList<>();

        Set<String> allowed = Set.of("x_value", "y_value", "function_id", "id");
        if (!allowed.contains(field)) throw new IllegalArgumentException();


        String sql = "SELECT * FROM points WHERE function_id = ? ORDER BY " + field + " " + direction;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, functionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                points.add(resultSetToPointEntity(rs));
            }
            logger.info("Найдено точек с сортировкой: {}", points.size());
        } catch (SQLException e) {
            logger.error("Ошибка поиска точек по function_id с сортировкой", e);
        }
        return points;
    }

    @Override
    public boolean update(PointEntity point) {
        String sql = "UPDATE points SET function_id = ?, x_value = ?, y_value = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, point.getFunction_id());
            stmt.setDouble(2, point.getX_value());
            stmt.setDouble(3, point.getY_value());
            stmt.setObject(4, point.getId());

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

    @Override
    public boolean delete(UUID id) {
        String sql = "DELETE FROM points WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
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

    @Override
    public boolean deleteByFunctionId(UUID functionId) {
        String sql = "DELETE FROM points WHERE function_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, functionId);
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

    private PointEntity resultSetToPointEntity(ResultSet rs) {
        try {
            logger.info("Создаём DTO Point на основе данных из БД");
            UUID id = rs.getObject("id", UUID.class);
            UUID functionId = rs.getObject("function_id", UUID.class);
            double xValue = rs.getDouble("x_value");
            double yValue = rs.getDouble("y_value");
            logger.info("DTO Point успешно создано");
            return new PointEntity(id, functionId, xValue, yValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
