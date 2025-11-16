package com.oop.labs.manual.dao;


import com.oop.labs.manual.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public int create(User user) {

        String query = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            int rs = stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Не удалось получить сгенерированный ключ");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания пользователя", e);
            throw new RuntimeException("Ошибка создания пользователя", e);
        }
    }

    public Optional<User> findById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = ResultSetToUser(rs);
                logger.info("Найден пользователь с id: {}", user.getId());
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя с id: {}", id, e);
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = ResultSetToUser(rs);
                logger.info("Найден пользователь с username: {}", user.getUsername());
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя с username: {}", username, e);
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                users.add(ResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска всех пользователей", e);
        }
        return users;
    }

    public List<User> findAllOrderedBy(String field, boolean isReversed) {
        String direction = isReversed ? "DESC" : "ASC";
        logger.info("Поиск всех пользователей с сортировкой по полю: {} направление: {}", field, direction);
        List<User> users = new ArrayList<>();
        
        String sql = "SELECT * FROM users ORDER BY " + field + " " + direction;

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                users.add(ResultSetToUser(rs));
            }
            logger.info("Найдено пользователей с сортировкой: {}", users.size());
        } catch (SQLException e) {
            logger.error("Ошибка поиска всех пользователей с сортировкой", e);
        }
        return users;
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setLong(3, user.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Обновлен пользователь с id: {}", user.getId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка обновления пользователя с id: {}", user.getId(), e);
        }
        return false;
    }

    public boolean delete(long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Удален пользователь с id: {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка удаления пользователя с id: {}", id, e);
        }
        return false;
    }

    public User ResultSetToUser(ResultSet rs){
        try {
            logger.info("Создаём DTO User на основе данных из БД");
            String username = rs.getString("username");
            String passwordHash = rs.getString("password_hash");
            long id = rs.getLong("id");
            logger.info("DTO User успешно создан");
            return new User(id, username, passwordHash);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}