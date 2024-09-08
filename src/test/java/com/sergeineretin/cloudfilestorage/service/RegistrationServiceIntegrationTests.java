package com.sergeineretin.cloudfilestorage.service;

import com.sergeineretin.cloudfilestorage.TestUtils;
import com.sergeineretin.cloudfilestorage.dto.UserDto;
import com.sergeineretin.cloudfilestorage.exception.UserAlreadyExistException;
import com.sergeineretin.cloudfilestorage.model.User;
import com.sergeineretin.cloudfilestorage.repository.UserRepository;
import com.sergeineretin.cloudfilestorage.util.Roles;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationServiceIntegrationTests {
    private final RegistrationService underTest;
    private final UserRepository userRepository;
    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(
            "mysql:8.0"
    );

    @Autowired
    public RegistrationServiceIntegrationTests(RegistrationService underTest,
                                               UserRepository userRepository) {
        this.underTest = underTest;
        this.userRepository = userRepository;
    }

    @BeforeAll
    static void beforeAll() {
        mysqlContainer.start();
    }

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    @AfterAll
    static void afterAll() {
        mysqlContainer.stop();
    }
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Test
    void testThatRegistrationServiceRegisterSavesNewToDatabase() {
        underTest.register(TestUtils.getUserDto());
        Optional<User> byId = userRepository.findById(TestUtils.getUserDto().getEmail());
        assertTrue(byId.isPresent());
        assertEquals("john.doe@gmail.com", byId.get().getEmail());
    }

    @Test
    void testThatRegistrationServiceRegisterSetsRoleUserToTheEntity() {
        underTest.register(TestUtils.getUserDto());
        Optional<User> byId = userRepository.findById(TestUtils.getUserDto().getEmail());
        assertTrue(byId.isPresent());
        assertEquals(Roles.ROLE_USER, byId.get().getRole());
    }

    @Test
    void testThatRegistrationServiceRegisterThrowsExceptionIfEmailAlreadyExists() {
        userRepository.save(TestUtils.getUser());
        assertThrows(UserAlreadyExistException.class, () -> underTest.register(TestUtils.getUserDto()));
    }

    @Test
    void testThatRegistrationServiceGetUserByLoginReturnsUserIfUserExists() {
        User save = userRepository.save(TestUtils.getUser());
        Optional<UserDto> result = underTest.getUserByLogin(save.getEmail());
        assertTrue(result.isPresent());
        assertEquals(save.getEmail(), result.get().getEmail());
    }

    @Test
    void testThatRegistrationServiceGetUserByLoginReturnsUserIfUserNotExists() {
        Optional<UserDto> result = underTest.getUserByLogin("john.doe@gmail.com");
        assertTrue(result.isEmpty());
    }
}
