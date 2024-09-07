package com.sergeineretin.cloudfilestorage.repository;

import com.sergeineretin.cloudfilestorage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
