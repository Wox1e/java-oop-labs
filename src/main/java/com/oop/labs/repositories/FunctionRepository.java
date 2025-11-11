package com.oop.labs.repositories;

import com.oop.labs.entities.functionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FunctionRepository extends JpaRepository<functionEntity, UUID> {

}
