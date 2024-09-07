package com.sergeineretin.cloudfilestorage.model;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @Column(name="email")
    private String email;
    @Column(name = "first_name")
    @NotEmpty(message = "The first name should not be blank")
    private String firstName;
    @Column(name = "last_name")
    @NotEmpty(message = "The last name should not be blank")
    private String lastName;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;
}
