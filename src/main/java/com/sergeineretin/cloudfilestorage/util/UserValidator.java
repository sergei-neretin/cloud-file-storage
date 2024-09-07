package com.sergeineretin.cloudfilestorage.util;

import com.sergeineretin.cloudfilestorage.dto.UserDto;
import com.sergeineretin.cloudfilestorage.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class UserValidator implements Validator {
    private final RegistrationService registrationService;

    @Autowired
    public UserValidator(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return UserDto.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target,@NonNull Errors errors) {
        UserDto userDto = (UserDto)target;
        Optional<UserDto> result = registrationService.getUserByLogin(userDto.getEmail());
        if (result.isPresent()) {
            errors.rejectValue("email", "", "This email address is already in use");
        }
        if (!userDto.getPassword().equals(userDto.getMatchingPassword())) {
            errors.rejectValue("password", "", "This passwords do not match");
        }
        if (userDto.getPassword().length() < 6) {
            errors.rejectValue("password", "", "This password is too short");
        }
    }
}
