package com.oop.labs.repository_tests;

import com.oop.labs.entities.functionEntity;
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
import com.oop.labs.repositories.FunctionRepository;

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
class FunctionRepositoryTest {

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
    private FunctionRepository functionRepository;
    final UUID ownerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        functionRepository.deleteAll();
    }

    @Test
    void shouldSaveFunction() {
        functionEntity newFunction = new functionEntity();
        newFunction.setName("mainFunc");
        newFunction.setType("type1");
        newFunction.setAuthor_id(ownerId);
        functionEntity savedFunc = functionRepository.save(newFunction);
        assertThat(savedFunc).isNotNull();
        assertThat(savedFunc.getId()).isNotNull();
        assertEquals(savedFunc.getName(), newFunction.getName());
    }
    
    @Test
    void shouldFindFunctionById() {
        functionEntity f = new functionEntity();
        f.setName("func_name");
        f.setType("func_tyoe");
        f.setAuthor_id(ownerId);
        functionEntity saved = functionRepository.save(f);
        Optional<functionEntity> found = functionRepository.findById(saved.getId());
        assertEquals(saved.getName(), found.get().getName());
        assertEquals(saved.getType(), found.get().getType());
    }

    @Test
    void shouldFindAllFunctions() {
        functionRepository.deleteAll();
        for (int i = 0; i < 5; i++) {
            functionEntity f = new functionEntity();
            f.setName("f"+i);
            f.setType("t"+i);
            f.setAuthor_id(ownerId);
            functionRepository.save(f);
        }
        assertEquals(functionRepository.findAll().size(), 5);
    }

    @Test
    void shouldFindByNameTypeAndAuthor() {
        functionEntity first = new functionEntity();
        first.setName("alpha");
        first.setType("polynomial");
        first.setAuthor_id(ownerId);
        functionRepository.save(first);

        functionEntity second = new functionEntity();
        second.setName("beta");
        second.setType("polynomial");
        second.setAuthor_id(ownerId);
        functionRepository.save(second);

        functionEntity third = new functionEntity();
        third.setName("gamma");
        third.setType("exponential");
        third.setAuthor_id(UUID.randomUUID());
        functionRepository.save(third);

        List<functionEntity> byName = functionRepository.findByName("alpha");
        List<functionEntity> byType = functionRepository.findByType("polynomial");
        List<functionEntity> byAuthor = functionRepository.findByAuthorId(ownerId);

        assertThat(byName).hasSize(1);
        assertEquals("alpha", byName.getFirst().getName());

        assertThat(byType).hasSize(2);


        assertThat(byAuthor).hasSize(2);
    }
}
