package com.usermanagement.identity_srv.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.usermanagement.identity_srv.dto.UserCreateRequest;
import com.usermanagement.identity_srv.model.User;
import com.usermanagement.identity_srv.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository repository;

  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  public List<User> getAllUsers() {
    return repository.findAll();
  }

  public User createUser(UserCreateRequest request) {
    User user = new User();
    user.setEmail(request.getEmail());
    user.setUsername(request.getUsername());
    user.setPassword(request.getPassword()); // ⚠️ Esto luego se debe hashear
    return repository.save(user);
  }

}
