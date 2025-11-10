package dao;

import entities.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserDAOInterface {
    UUID create(UserEntity user);
    UserEntity findById(UUID id) ;
    UserEntity findByUsername(String username);
    List<UserEntity> findAll();
    List<UserEntity> findAllOrderedBy(String field, boolean isReversed);
}
