package com.sergeineretin.cloudfilestorage.service;

import com.sergeineretin.cloudfilestorage.dto.UserDto;
import com.sergeineretin.cloudfilestorage.exception.StorageException;
import com.sergeineretin.cloudfilestorage.exception.UserAlreadyExistException;
import com.sergeineretin.cloudfilestorage.model.User;
import com.sergeineretin.cloudfilestorage.repository.UserRepository;
import com.sergeineretin.cloudfilestorage.util.Roles;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static com.sergeineretin.cloudfilestorage.util.StorageUtils.USER_FILES_BUCKET_NAME;

@Service
public class RegistrationService {
    private static final Logger log = LoggerFactory.getLogger(HomeService.class);


    private final UserRepository repository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final MinioClient minioClient;
    @Autowired
    public RegistrationService(ModelMapper modelMapper,
                               UserRepository repository,
                               MinioClient minioClient,
                               PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.minioClient = minioClient;
    }

    public Optional<UserDto> getUserByLogin(String login) {
        Optional<User> model = repository.findByUsername(login);
        return model.map(user -> modelMapper.map(user, UserDto.class));
    }

    @Transactional
    public void register(UserDto userDto) {
        if (getUserByLogin(userDto.getEmail()).isEmpty()) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userDto.setRole(Roles.ROLE_USER);
            User user = modelMapper.map(userDto, User.class);
            repository.save(user);
            createFolder(user.getId());
        } else {
            throw new UserAlreadyExistException("User already exists.");
        }
    }

    private void createFolder(Long id) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(USER_FILES_BUCKET_NAME)
                            .object("user-" + id + "-files/")
                            .stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                            .build());
        } catch (Exception e) {
            log.error("Error creating folder '{}'", "user-" + id + "-files/", e);
            throw new StorageException("Failed to store folder 'user-" + id + "-files/'", e);
        }
    }
}
