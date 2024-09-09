package com.sergeineretin.cloudfilestorage.model;

import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Size(min = 2, max = 50, message = "Username length must be in between 2 and 50 symbols")
    @Column(name="email", unique=true)
    private String email;

    @Column(name = "first_name")
    @Size(min = 1, max = 50, message = "First name length must be in between 2 and 50 symbols")
    private String firstName;

    @Column(name = "last_name")
    @Size(min = 1, max = 50, message = "Last name length must be in between 2 and 50 symbols")
    private String lastName;

    @Column(name = "password")
    @Size(min = 8, max = 100, message = "Username length must be in between 2 and 50 symbols")
    private String password;

    @Column(name = "role")
    private String role;
}
