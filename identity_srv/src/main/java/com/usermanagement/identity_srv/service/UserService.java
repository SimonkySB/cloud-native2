package com.usermanagement.identity_srv.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.usermanagement.identity_srv.dto.LoginRequest;
import com.usermanagement.identity_srv.dto.LoginResponse;
import com.usermanagement.identity_srv.dto.UpdateUserRequest;
import com.usermanagement.identity_srv.dto.UserCreateRequest;
import com.usermanagement.identity_srv.model.User;
import com.usermanagement.identity_srv.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AzureFunctionService azureFunctionService;

  public UserService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AzureFunctionService azureFunctionService) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.azureFunctionService = azureFunctionService;
  }

  public List<User> getAllUsers() {
    return repository.findAll();
  }

  public User createUser(UserCreateRequest request) {
    if (repository.findByEmail(request.getEmail()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
    }

    User user = new User();
    user.setEmail(request.getEmail());
    user.setUsername(request.getUsername());

    String hashedPassword = passwordEncoder.encode(request.getPassword());
    user.setPassword(hashedPassword);

    return repository.save(user);
  }

  public LoginResponse login(LoginRequest request) {
    User user = repository.findByEmail(request.getEmail())
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
    if(userLastLogin != null && userLastLogin.isAfter(now.minusMinutes(1))){
      try {
        azureFunctionService.ExecuteSuspiciousActivityFor(user.getEmail());
      } catch(Exception ex) {
        ex.printStackTrace();
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error");
      }
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You have attempted to log in too frequently. Please wait a moment.");
    }

    user.setLastLogin(now);
    repository.save(user);

    return new LoginResponse(token);
  }

  public void updateUserStatus(Long userId, boolean active) {
    User user = repository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    user.setActive(active);
    repository.save(user);
  }

  public void updateUser(Long id, UpdateUserRequest request) {
    User user = repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());

    repository.save(user);
  }

  public User getUserByEmail(String email) {
    return repository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }


  
}