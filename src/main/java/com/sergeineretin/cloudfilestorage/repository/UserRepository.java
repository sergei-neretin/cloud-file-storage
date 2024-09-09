package com.sergeineretin.cloudfilestorage.repository;

import com.sergeineretin.cloudfilestorage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query(value = "SELECT t FROM User t WHERE t.email = :email")
    Optional<User> findByUsername(String email);
}
