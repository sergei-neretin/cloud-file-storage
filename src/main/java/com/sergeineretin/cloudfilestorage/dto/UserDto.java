package com.sergeineretin.cloudfilestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {
    private long id;

    private String firstName;

    private String lastName;

    private String password;

    private String matchingPassword;

    private String email;

    private String role;
}
