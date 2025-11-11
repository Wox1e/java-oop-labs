package dao;

import entities.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class UserDAO implements UserDAOInterface{
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UUID create(UserEntity user) {

        String query = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword_hash());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Ошибка создания пользователя");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    UUID id = generatedKeys.getObject(1, UUID.class);
                    logger.info("Создан пользователь с id: {}", id);
                    return id;
                } else {
                    throw new SQLException("Ошибка создания пользователя, id не получен");
                }
            }

        } catch (SQLException e) {
            logger.error("Ошибка создания пользователя", e);
            throw new RuntimeException("Ошибка создания пользователя", e);
        }
    }

    @Override
    public boolean updatePassword_hash(UserEntity user, String newPassword) {
        String query = "UPDATE users " +
                "SET password_hash = ? " +
                "WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setObject(2, user.getId());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated != 0) {
                user.setPassword_hash(newPassword);
                logger.info("Пароль пользователя {} был обновлён", user.getUsername());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка обновления пароля пользователя", e);
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean updateUsername(UserEntity user, String newUsername) {
        String query = "UPDATE users " +
                "SET username = ? " +
                "WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setObject(2, user.getId());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated != 0) {
                user.setUsername(newUsername);
                logger.info("Имя пользователя с id:{} был обновлён", user.getId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка обновления пароля пользователя", e);
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserEntity user = ResultSetToUserEntity(rs);
                logger.info("Найден пользователь с id: {}", user.getId());
                return Optional.of(user);
            }
        } catch (SQLException e) {
        logger.error("Ошибка поиска пользователя с id: {}", id, e);
    }
        return Optional.empty();
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UserEntity user = ResultSetToUserEntity(rs);
                logger.info("Найден пользователь с username: {}", user.getUsername());
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя с username: {}", username, e);
        }

        return Optional.empty();
    }

    @Override
    public List<UserEntity> findAll() {
        List<UserEntity> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(ResultSetToUserEntity(rs));
            }
            return users;
        } catch (SQLException e) {
            logger.error("Ошибка поиска всех пользователей", e);
        }
        return List.of();
    }

    @Override
    public List<UserEntity> findAllOrderedBy(String field, boolean isReversed) {
        String direction = isReversed ? "DESC" : "ASC";
        logger.info("Поиск всех пользователей с сортировкой по полю: {} направление: {}", field, direction);
        List<UserEntity> users = new ArrayList<>();

        Set<String> allowed = Set.of("username", "password_hash", "id");
        if (!allowed.contains(field)) throw new IllegalArgumentException();

        String query = "SELECT * FROM users ORDER BY " + field + " " + direction;

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                users.add(ResultSetToUserEntity(rs));
            }
            logger.info("Найдено пользователей с сортировкой: {}", users.size());
        } catch (SQLException e) {
            logger.error("Ошибка поиска всех пользователей с сортировкой", e);
        }
        return users;
    }

    @Override
    public boolean deleteUser(UserEntity user) {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, user.getId());
            int rowsDeleted = stmt.executeUpdate();  // executeUpdate для DELETE
            if (rowsDeleted > 0) {
                logger.info("Пользователь с id {} успешно удалён", user.getId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Ошибка удаления пользователя", e);
            throw new RuntimeException("Ошибка удаления пользователя", e);
        }
        return false;
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
