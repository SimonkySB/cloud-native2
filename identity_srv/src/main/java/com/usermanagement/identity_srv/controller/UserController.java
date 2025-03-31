package com.usermanagement.identity_srv.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usermanagement.identity_srv.dto.AssignRoles;
import com.usermanagement.identity_srv.dto.LoginRequest;
import com.usermanagement.identity_srv.dto.LoginResponse;
import com.usermanagement.identity_srv.dto.UpdateUserStatusRequest;
import com.usermanagement.identity_srv.dto.UserDto;
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
  public ResponseEntity<List<User>> getAllUsers() {
    var users = service.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @PostMapping
  public ResponseEntity<User> create(@RequestBody UserDto dto) {
    return ResponseEntity.ok(service.create(dto));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> updateUserStatus(@PathVariable Long id,
      @RequestBody UpdateUserStatusRequest request) {
    service.updateUserStatus(id, request.isActive());
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserDto dto) {
    return ResponseEntity.ok(service.update(id, dto));
  }

  @GetMapping("/me")
  public ResponseEntity<User> getAuthenticatedUser() {
    String email = AuthUtils.getCurrentUserEmail();

    User user = service.getUserByEmail(email);

    return ResponseEntity.ok(user);
  }

  @PutMapping("/{id}/roles")
  public ResponseEntity<Void> assignRoles(@PathVariable Long id, @RequestBody AssignRoles dto) {
    service.assignRolesToUser(id, dto.roleIds());
    return ResponseEntity.ok().build();
  }

}
