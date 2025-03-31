package com.usermanagement.identity_srv.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.usermanagement.identity_srv.dto.LoginRequest;
import com.usermanagement.identity_srv.dto.LoginResponse;
import com.usermanagement.identity_srv.dto.UserDto;
import com.usermanagement.identity_srv.model.Role;
import com.usermanagement.identity_srv.model.User;
import com.usermanagement.identity_srv.repository.RoleRepository;
import com.usermanagement.identity_srv.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AzureFunctionService azureFunctionService;

  public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
      JwtService jwtService,
      AzureFunctionService azureFunctionService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.azureFunctionService = azureFunctionService;
    this.roleRepository = roleRepository;
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional
  public User create(UserDto dto) {
    User user = new User(dto.email(), dto.username(), dto.password());
    user.setActive(dto.active());

    if (dto.roleIds() != null && !dto.roleIds().isEmpty()) {
      List<Role> roles = roleRepository.findAllById(dto.roleIds());
      user.setRoles(new HashSet<>(roles));
    }

    return userRepository.save(user);
  }

  @Transactional
  public User update(Long id, UserDto dto) {
    User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

    user.setUsername(dto.username());
    user.setEmail(dto.email());

    if (dto.roleIds() != null) {
      List<Role> roles = roleRepository.findAllById(dto.roleIds());
      user.setRoles(new HashSet<>(roles));
    }

    return userRepository.save(user);
  }

  public LoginResponse login(LoginRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

    if (!user.isActive()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is inactive");
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    String token = jwtService.generateToken(user.getEmail());

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime userLastLogin = user.getLastLogin();

    // Verifica si el usuario intentó iniciar sesión en menos de 1 minuto
    if (userLastLogin != null && userLastLogin.isAfter(now.minusMinutes(1))) {
      try {
        azureFunctionService.ExecuteSuspiciousActivityFor(user.getEmail());
      } catch (Exception ex) {
        ex.printStackTrace();
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error");
      }
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
          "You have attempted to log in too frequently. Please wait a moment.");
    }

    user.setLastLogin(now);
    userRepository.save(user);

    return new LoginResponse(token);
  }

  public void updateUserStatus(Long userId, boolean active) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    user.setActive(active);
    userRepository.save(user);
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  @Transactional
  public void assignRolesToUser(Long userId, List<Long> roleIds) {
    User user = userRepository.findById(userId).orElseThrow();
    List<Role> roles = roleRepository.findAllById(roleIds);
    user.setRoles(new HashSet<>(roles));
    userRepository.save(user);
  }
}