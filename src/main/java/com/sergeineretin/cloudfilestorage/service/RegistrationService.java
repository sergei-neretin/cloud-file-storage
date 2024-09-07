package com.sergeineretin.cloudfilestorage.service;

import com.sergeineretin.cloudfilestorage.dto.UserDto;
import com.sergeineretin.cloudfilestorage.model.User;
import com.sergeineretin.cloudfilestorage.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegistrationService {
    private final UserRepository repository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public RegistrationService(ModelMapper modelMapper,
                               UserRepository repository,
                               PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserDto> getUserByLogin(String login) {
        Optional<User> model = repository.findById(login);
        return model.map(user -> modelMapper.map(user, UserDto.class));
    }

    @Transactional
    public void register(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = modelMapper.map(userDto, User.class);
        repository.save(user);
    }

}
