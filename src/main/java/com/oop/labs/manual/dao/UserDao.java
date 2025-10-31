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

        String query = "INSERT INTO users (username, password_hash) VALUES (?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                int id = rs.getInt("id");
                logger.info("Создан пользователь с id: {}", id);
                return id;
            }

        } catch (SQLException e) {
            logger.error("Ошибка создания пользователя", e);
            throw new RuntimeException("Ошибка создания пользователя", e);
        }

        return -1;
    }

    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
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

    public User ResultSetToUser(ResultSet rs){
        try {
            logger.info("Создаём DTO User на основе данных из БД");
            String username = rs.getString("username");
            String passwordHash = rs.getString("password_hash");
            int id = rs.getInt("id");
            logger.info("DTO User успешно создан");
            return new User(id, username, passwordHash);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}