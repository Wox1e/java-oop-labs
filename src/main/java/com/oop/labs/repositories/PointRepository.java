package com.oop.labs.repositories;

import com.oop.labs.entities.pointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PointRepository extends JpaRepository<pointEntity, UUID> {

}
