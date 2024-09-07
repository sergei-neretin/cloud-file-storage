package com.sergeineretin.cloudfilestorage.service;

import com.sergeineretin.cloudfilestorage.model.User;
import com.sergeineretin.cloudfilestorage.repository.UserRepository;
import com.sergeineretin.cloudfilestorage.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User " + email + " not found");
        }
        return new UserDetailsImpl(user.get());
    }
}
