package com.oop.labs.repositories;

import com.oop.labs.entities.userEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<userEntity, UUID> {

}
