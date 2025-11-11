package dao;

import entities.PointEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PointDAOInterface {
    UUID create(PointEntity point);

    Optional<PointEntity> findById(UUID id);

    List<PointEntity> findByFunctionId(UUID functionId);

    List<PointEntity> findAll();

    List<PointEntity> findAllOrderedBy(String field, boolean isReversed);

    List<PointEntity> findByFunctionIdOrderedBy(UUID functionId, String field, boolean isReversed);

    boolean update(PointEntity point);

    boolean delete(UUID id);

    boolean deleteByFunctionId(UUID functionId);
}
