package dao;

import entities.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDAOInterface {

    UUID create(UserEntity user);

    boolean updatePassword_hash(UserEntity user, String newPassword);

    boolean updateUsername(UserEntity user, String newUsername);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAll();

    List<UserEntity> findAllOrderedBy(String field, boolean isReversed);

    boolean deleteUser(UserEntity user);
}
