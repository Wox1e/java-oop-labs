package com.oop.labs.repository_tests;

import com.oop.labs.entities.userEntity;
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
import com.oop.labs.repositories.UserRepository;

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
class UserRepositoryTest {

    @org.springframework.context.annotation.Configuration
    static class Config {}

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
    private UserRepository userRepository;

    private userEntity testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveUser() {
        userEntity newUser = new userEntity();
        newUser.setUsername("newuser");
        newUser.setPassword_hash("123");

        userEntity savedUser = userRepository.save(newUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
    }

    @Test
    void shouldFindUserById() {
        userEntity newUser = new userEntity();
        newUser.setUsername("newuser");
        newUser.setPassword_hash("123");
        userEntity savedUser = userRepository.save(newUser);
        UUID targetID = savedUser.getId();

        Optional<userEntity> foundUser = userRepository.findById(targetID);

        assertEquals(foundUser.get().getUsername(), savedUser.getUsername());
        assertEquals(foundUser.get().getPassword_hash(), savedUser.getPassword_hash());
        assertEquals(foundUser.get().getId(), savedUser.getId());

    }

    @Test
    void shouldFindAllUsers() {
        userRepository.deleteAll();

        for (int i = 0; i < 14; i++) {
            userEntity newUser = new userEntity();
            newUser.setUsername("newuser" + i);
            newUser.setPassword_hash("123");
            userEntity savedUser = userRepository.save(newUser);
        }

        assertEquals(userRepository.findAll().size(), 14);

    }

    @Test
    void shouldUpdateUser() {
       userEntity newUser = new userEntity();
       newUser.setUsername("newuser");
       newUser.setPassword_hash("123");
       userEntity savedUser = userRepository.save(newUser);

       savedUser.setUsername("updateduser");
       savedUser.setPassword_hash("updatedhash");
       userRepository.saveAndFlush(savedUser);
       assertEquals("updateduser", newUser.getUsername());
       assertEquals("updatedhash", newUser.getPassword_hash());
    }

    @Test
    void shouldDeleteUser() {
        userEntity newUser = new userEntity();
        newUser.setUsername("newuser");
        newUser.setPassword_hash("123");
        userEntity savedUser = userRepository.save(newUser);
        userRepository.delete(savedUser);
        Optional<userEntity> foundUser = userRepository.findById(savedUser.getId());
        assertEquals(foundUser, Optional.empty());
    }


    @Test
    void createPerformanceTest() {
        int batchSize = 15000;
        List<userEntity> toSave = new java.util.ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            userEntity user = new userEntity();
            user.setUsername("testuser" + i);
            user.setPassword_hash("123");
            toSave.add(user);
        }
        long start = System.currentTimeMillis();
        userRepository.saveAll(toSave);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Time to save 15k records: " + elapsed + " ms");
    }

    @Test
    void deletePerformanceTest() {
        int batchSize = 15000;
        List<userEntity> toSave = new java.util.ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            userEntity user = new userEntity();
            user.setUsername("deluser" + i);
            user.setPassword_hash("pw");
            toSave.add(user);
        }
        userRepository.saveAll(toSave);
        List<userEntity> all = userRepository.findAll();
        long start = System.currentTimeMillis();
        userRepository.deleteAll(all);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("ds to delete 15k users: " + elapsed + " ms");
        assertEquals(userRepository.count(), 0);
    }

    @Test
    void updatePerformanceTest() {
        int batchSize = 15000;
        List<userEntity> toSave = new java.util.ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            userEntity user = new userEntity();
            user.setUsername("upduser" + i);
            user.setPassword_hash("pwA");
            toSave.add(user);
        }
        userRepository.saveAll(toSave);
        List<userEntity> all = userRepository.findAll();
        for (userEntity user : all) {
            user.setPassword_hash("pwB");
        }
        long start = System.currentTimeMillis();
        userRepository.saveAll(all);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Time to update 15k users: " + elapsed + " ms");
        assertThat(userRepository.findAll().stream().allMatch(u -> u.getPassword_hash().equals("pwB"))).isTrue();
    }

}