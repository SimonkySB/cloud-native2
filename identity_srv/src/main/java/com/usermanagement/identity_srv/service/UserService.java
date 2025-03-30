package com.usermanagement.identity_srv.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.usermanagement.identity_srv.dto.UserCreateRequest;
import com.usermanagement.identity_srv.model.User;
import com.usermanagement.identity_srv.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  public List<User> getAllUsers() {
    return repository.findAll();
  }

  public User createUser(UserCreateRequest request) {
    User user = new User();
    user.setEmail(request.getEmail());
    user.setUsername(request.getUsername());

    String hashedPassword = passwordEncoder.encode(request.getPassword());
    user.setPassword(hashedPassword);

    return repository.save(user);
  }
}