package com.hcm.authservice.service;

import com.hcm.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.hcm.authservice.model.User;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
