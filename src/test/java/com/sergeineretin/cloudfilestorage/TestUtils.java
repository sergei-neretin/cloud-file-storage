package com.sergeineretin.cloudfilestorage;

import com.sergeineretin.cloudfilestorage.dto.UserDto;
import com.sergeineretin.cloudfilestorage.model.User;

public class TestUtils {
    private TestUtils() {}

    public static UserDto getUserDto() {
        return UserDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .password("secret")
                .matchingPassword("secret")
                .build();
    }
    public static User getUser() {
        return User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .password("secret")
                .build();
    }
}
