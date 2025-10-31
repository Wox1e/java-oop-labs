package com.oop.labs.manual.dao;

import com.oop.labs.manual.dto.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FunctionDao {
    private static final Logger logger = LoggerFactory.getLogger(FunctionDao.class);
    private final Connection connection;

    public FunctionDao(Connection connection) {
        this.connection = connection;
    }

    public int create(Function function) {
        String query = "INSERT INTO functions (name, type, author_id) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, function.getName());
            stmt.setString(2, function.getType());
            stmt.setInt(3, function.getAuthorId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                logger.info("Создана функция с id: {}", id);
                return id;
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания функции", e);
            throw new RuntimeException("Ошибка создания функции", e);
        }
        return -1;
    }

    public Optional<Function> findById(int id) {
        String sql = "SELECT * FROM functions WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Function function = resultSetToFunction(rs);
                logger.info("Найдена функция с id: {}", function.getId());
                return Optional.of(function);
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска функции с id: {}", id, e);
        }
        return Optional.empty();
    }

    public Optional<Function> findByName(String name) {
        String sql = "SELECT * FROM functions WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Function function = resultSetToFunction(rs);
                logger.info("Найдена функция с name: {}", function.getName());
                return Optional.of(function);
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска функции с name: {}", name, e);
        }
        return Optional.empty();
    }

    public List<Function> findByAuthorId(int authorId) {
        List<Function> functions = new ArrayList<>();
        String sql = "SELECT * FROM functions WHERE author_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                functions.add(resultSetToFunction(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска функций по author_id: {}", authorId, e);
        }
        return functions;
    }

    public List<Function> findAll() {
        List<Function> functions = new ArrayList<>();
        String sql = "SELECT * FROM functions";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                functions.add(resultSetToFunction(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска всех ф-ций", e);
        }
        return functions;
    }

    public boolean update(Function function) {
        String sql = "UPDATE functions SET name = ?, type = ?, author_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, function.getName());
            stmt.setString(2, function.getType());
            stmt.setInt(3, function.getAuthorId());
            stmt.setInt(4, function.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Обновлена функция с id: {}", function.getId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка обновления функции с id: {}", function.getId(), e);
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM functions WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Удалена функция с id: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка удаления функции с id: {}", id, e);
        }
        return false;
    }

    private Function resultSetToFunction(ResultSet rs) {
        try {
            logger.info("Пытаемся создать DTO Function на основе данных из БД");
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String type = rs.getString("type");
            int authorId = rs.getInt("author_id");
            logger.info("DTO Function успешно создано");
            return new Function(id, name, type, authorId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}