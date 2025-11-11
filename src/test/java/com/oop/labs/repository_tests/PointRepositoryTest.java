package com.oop.labs.repository_tests;

import com.oop.labs.entities.pointEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.oop.labs.repositories.PointRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EntityScan("com.oop.labs.entities")
@EnableJpaRepositories("com.oop.labs.repositories")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PointRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    @Autowired
    private PointRepository pointRepository;
    @BeforeEach
    void setUp() { pointRepository.deleteAll(); }

    @Test
    void shouldSavePoint() {
        pointEntity point = new pointEntity();
        point.setFunction_id(UUID.randomUUID());
        point.setX_value(1.11);
        point.setY_value(2.22);
        pointEntity saved = pointRepository.save(point);
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertEquals(saved.getX_value(), 1.11);
    }
    @Test
    void shouldFindPointById() {
        pointEntity p = new pointEntity();
        p.setFunction_id(UUID.randomUUID());
        p.setX_value(2.3);
        p.setY_value(4.5);
        pointEntity saved = pointRepository.save(p);
        Optional<pointEntity> found = pointRepository.findById(saved.getId());
        assertEquals(saved.getX_value(), found.get().getX_value());
    }
    @Test
    void shouldFindAllPoints() {
        pointRepository.deleteAll();
        for (int i = 0; i < 6; i++) {
            pointEntity p = new pointEntity();
            p.setFunction_id(UUID.randomUUID());
            p.setX_value(i);
            p.setY_value(i+0.1);
            pointRepository.save(p);
        }
        assertEquals(pointRepository.findAll().size(), 6);
    }
    @Test
    void shouldUpdatePoint() {
        pointEntity p = new pointEntity();
        p.setFunction_id(UUID.randomUUID());
        p.setX_value(3.3);
        p.setY_value(4.4);
        pointEntity saved = pointRepository.save(p);
        saved.setX_value(100.11);
        pointRepository.saveAndFlush(saved);
        pointEntity updated = pointRepository.findById(saved.getId()).orElseThrow();
        assertEquals(updated.getX_value(), 100.11);
    }
    @Test
    void shouldDeletePoint() {
        pointEntity p = new pointEntity();
        p.setFunction_id(UUID.randomUUID());
        p.setX_value(9);
        p.setY_value(3);
        pointEntity saved = pointRepository.save(p);
        pointRepository.delete(saved);
        Optional<pointEntity> found = pointRepository.findById(saved.getId());
        assertEquals(found, Optional.empty());
    }

}
