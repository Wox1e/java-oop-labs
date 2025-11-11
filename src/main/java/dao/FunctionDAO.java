package dao;

import entities.FunctionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class FunctionDAO implements FunctionDAOInterface{
    private static final Logger logger = LoggerFactory.getLogger(FunctionDAO.class);
    private final Connection connection;

    public FunctionDAO(Connection connection) {
        this.connection = connection;
    }

    public UUID create(FunctionEntity function) {
        String query = "INSERT INTO functions (name, type, author_id) VALUES (?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, function.getName());
            stmt.setString(2, function.getType());
            stmt.setObject(3, function.getAuthor_id());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID id = rs.getObject("id", UUID.class);
                logger.info("Создана функция с id: {}", id);
                return id;
            }
        } catch (SQLException e) {
            logger.error("Ошибка создания функции", e);
            throw new RuntimeException("Ошибка создания функции", e);
        }
        return null;
    }

    @Override
    public Optional<FunctionEntity> findById(UUID id) {
        String sql = "SELECT * FROM functions WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                FunctionEntity function = resultSetToFunction(rs);
                logger.info("Найдена функция с id: {}", function.getId());
                return Optional.of(function);
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска функции с id: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<FunctionEntity> findByName(String name) {
        String sql = "SELECT * FROM functions WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                FunctionEntity function = resultSetToFunction(rs);
                logger.info("Найдена функция с name: {}", function.getName());
                return Optional.of(function);
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска функции с name: {}", name, e);
        }
        return Optional.empty();
    }

    @Override
    public List<FunctionEntity> findByAuthorId(UUID authorId) {
        List<FunctionEntity> functions = new ArrayList<>();
        String sql = "SELECT * FROM functions WHERE author_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, authorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                functions.add(resultSetToFunction(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска функций по author_id: {}", authorId, e);
        }
        return functions;
    }

    @Override
    public List<FunctionEntity> findAll() {
        List<FunctionEntity> functions = new ArrayList<>();
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

    @Override
    public List<FunctionEntity> findAllOrderedBy(String field, boolean isReversed) {
        String direction = isReversed ? "DESC" : "ASC";
        logger.info("Поиск всех функций с сортировкой по полю: {} направление: {}", field, direction);
        List<FunctionEntity> functions = new ArrayList<>();

        Set<String> allowed = Set.of("name", "type", "author_id", "id");
        if (!allowed.contains(field)) throw new IllegalArgumentException();


        String sql = "SELECT * FROM functions ORDER BY " + field + " " + direction;

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                functions.add(resultSetToFunction(rs));
            }
            logger.info("Найдено функций с сортировкой: {}", functions.size());
        } catch (SQLException e) {
            logger.error("Ошибка поиска всех функций с сортировкой", e);
        }
        return functions;
    }

    @Override
    public List<FunctionEntity> findByAuthorIdOrderedBy(UUID authorId, String field, boolean isReversed) {
        String direction = isReversed ? "DESC" : "ASC";
        logger.info("Поиск функций по author_id: {} с сортировкой по полю: {} направление: {}", authorId, field, direction);
        List<FunctionEntity> functions = new ArrayList<>();

        Set<String> allowed = Set.of("name", "type", "author_id", "id");
        if (!allowed.contains(field)) throw new IllegalArgumentException();

        String sql = "SELECT * FROM functions WHERE author_id = ? ORDER BY " + field + " " + direction;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, authorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                functions.add(resultSetToFunction(rs));
            }
            logger.info("Найдено функций с сортировкой: {}", functions.size());
        } catch (SQLException e) {
            logger.error("Ошибка поиска функций по author_id с сортировкой", e);
        }
        return functions;
    }

    @Override
    public boolean update(FunctionEntity function) {
        String sql = "UPDATE functions SET name = ?, type = ?, author_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, function.getName());
            stmt.setString(2, function.getType());
            stmt.setObject(3, function.getAuthor_id());
            stmt.setObject(4, function.getId());

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

    @Override
    public boolean delete(UUID id) {
        String sql = "DELETE FROM functions WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
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

    private FunctionEntity resultSetToFunction(ResultSet rs) {
        try {
            logger.info("Пытаемся создать DTO Function на основе данных из БД");
            UUID id = rs.getObject("id", UUID.class);
            String name = rs.getString("name");
            String type = rs.getString("type");
            UUID authorId = rs.getObject("author_id", UUID.class);
            logger.info("DTO Function успешно создано");
            return new FunctionEntity(id, name, type, authorId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
