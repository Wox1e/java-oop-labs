package com.oop.labs.repositories;

import com.oop.labs.entities.functionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FunctionRepository extends JpaRepository<functionEntity, UUID> {

    List<functionEntity> findByName(String name);
    List<functionEntity> findByAuthorId(UUID authorId);
    List<functionEntity> findByType(String type);
}
