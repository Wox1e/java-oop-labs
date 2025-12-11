package com.oop.labs.repositories;

import com.oop.labs.entities.userEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<userEntity, UUID> {
    Optional<userEntity> findByUsername(String username);


}
