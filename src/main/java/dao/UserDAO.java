package dao;

import entities.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public UUID create(UserEntity user) {

        String query = "INSERT INTO users (username, password_hash) VALUES (?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword_hash());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                logger.info("Создан пользователь с id: {}", id);
                return id;
            }

        } catch (SQLException e) {
            logger.error("Ошибка создания пользователя", e);
            throw new RuntimeException("Ошибка создания пользователя", e);
        }
        return null;
    }

    public UserEntity findById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserEntity user = ResultSetToUserEntity(rs);
                logger.info("Найден пользователь с id: {}", user.getId());
                return user;
            }
        } catch (SQLException e) {
        logger.error("Ошибка поиска пользователя с id: {}", id, e);
    }
        return null;
    }

    public UserEntity findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserEntity user = ResultSetToUserEntity(rs);
                logger.info("Найден пользователь с username: {}", user.getUsername());
                return user;
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя с username: {}", username, e);
        }

        return null;
    }

    public List<UserEntity> findAll() {
        List<UserEntity> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(ResultSetToUserEntity(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска всех пользователей", e);
        }
        return List.of();
    }

    public List<UserEntity> findAllOrderedBy(String field, boolean isReversed) {
        String direction = isReversed ? "DESC" : "ASC";
        logger.info("Поиск всех пользователей с сортировкой по полю: {} направление: {}", field, direction);
        List<UserEntity> users = new ArrayList<>();

        String sql = "SELECT * FROM users ORDER BY " + field + " " + direction;

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                users.add(ResultSetToUserEntity(rs));
            }
            logger.info("Найдено пользователей с сортировкой: {}", users.size());
        } catch (SQLException e) {
            logger.error("Ошибка поиска всех пользователей с сортировкой", e);
        }
        return users;
    }

    private UserEntity ResultSetToUserEntity(ResultSet rs){
        try {
            logger.info("Создаём UserEntity на основе данных из БД");
            String username = rs.getString("username");
            String passwordHash = rs.getString("password_hash");
            UUID id = rs.getObject("id", UUID.class);
            logger.info("UserEntity успешно создан");
            return new UserEntity(id, username, passwordHash);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
