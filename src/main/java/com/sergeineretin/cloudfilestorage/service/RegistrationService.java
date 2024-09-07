package com.sergeineretin.cloudfilestorage.service;

import com.sergeineretin.cloudfilestorage.dto.UserDto;
import com.sergeineretin.cloudfilestorage.model.User;
import com.sergeineretin.cloudfilestorage.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegistrationService {
    private final UserRepository repository;
    private final ModelMapper modelMapper;
    @Autowired
    public RegistrationService(ModelMapper modelMapper, UserRepository repository) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public Optional<UserDto> getUserByLogin(String login) {
        Optional<User> model = repository.findById(login);
        return model.map(user -> modelMapper.map(user, UserDto.class));
    }

    @Transactional
    public void register(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        repository.save(user);
    }

}
