package dao;

import entities.FunctionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FunctionDAOInterface {
    UUID create(FunctionEntity function);

    Optional<FunctionEntity> findById(UUID id);

    Optional<FunctionEntity> findByName(String name);

    List<FunctionEntity> findByAuthorId(UUID authorId);

    List<FunctionEntity> findAll();

    List<FunctionEntity> findAllOrderedBy(String field, boolean isReversed);

    List<FunctionEntity> findByAuthorIdOrderedBy(UUID authorId, String field, boolean isReversed);

    boolean update(FunctionEntity function);

    boolean delete(UUID id);
}
