package com.usermanagement.identity_srv.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usermanagement.identity_srv.dto.LoginRequest;
import com.usermanagement.identity_srv.dto.LoginResponse;
import com.usermanagement.identity_srv.dto.UpdateUserRequest;
import com.usermanagement.identity_srv.dto.UpdateUserStatusRequest;
import com.usermanagement.identity_srv.dto.UserCreateRequest;
import com.usermanagement.identity_srv.model.User;
import com.usermanagement.identity_srv.service.UserService;
import com.usermanagement.identity_srv.utils.AuthUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = service.login(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public List<User> getAllUsers() {
    return service.getAllUsers();
  }

  @PostMapping
  public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateRequest request) {
    User newUser = service.createUser(request);
    return new ResponseEntity<>(newUser, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> updateUserStatus(@PathVariable Long id,
      @RequestBody UpdateUserStatusRequest request) {
    service.updateUserStatus(id, request.isActive());
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateUser(@PathVariable Long id,
      @Valid @RequestBody UpdateUserRequest request) {
    service.updateUser(id, request);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public ResponseEntity<User> getAuthenticatedUser() {
    String email = AuthUtils.getCurrentUserEmail();

    User user = service.getUserByEmail(email);

    return ResponseEntity.ok(user);
  }

}
