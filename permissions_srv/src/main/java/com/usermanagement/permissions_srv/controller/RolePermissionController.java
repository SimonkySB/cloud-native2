package com.usermanagement.permissions_srv.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usermanagement.permissions_srv.model.Permission;
import com.usermanagement.permissions_srv.model.Role;
import com.usermanagement.permissions_srv.repository.PermissionRepository;
import com.usermanagement.permissions_srv.repository.RoleRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class RolePermissionController {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  public RolePermissionController(RoleRepository roleRepository, PermissionRepository permissionRepository) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
  }

  // -------------------- PERMISSIONS --------------------

  @GetMapping("/permissions")
  public ResponseEntity<List<Permission>> getAllPermissions() {
    return ResponseEntity.ok(permissionRepository.findAll());
  }

  // -------------------- ROLES --------------------

  @GetMapping("/roles")
  public ResponseEntity<List<Role>> getAllRoles() {
    return ResponseEntity.ok(roleRepository.findAll());
  }

  @GetMapping("/roles/{id}")
  public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
    return roleRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/roles")
  @Transactional
  public ResponseEntity<Role> createRole(@RequestBody RoleCreateRequest request) {
    Role role = new Role();
    role.setName(request.name());
    role.setCode(request.code());

    List<Permission> permissions = permissionRepository.findAllById(request.permissionIds());
    role.setPermissions(Set.copyOf(permissions));

    return ResponseEntity.ok(roleRepository.save(role));
  }

  @PutMapping("/roles/{id}")
  @Transactional
  public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
    Optional<Role> optionalRole = roleRepository.findById(id);
    if (optionalRole.isEmpty())
      return ResponseEntity.notFound().build();

    Role role = optionalRole.get();
    role.setName(request.name());
    role.setCode(request.code());
    List<Permission> permissions = permissionRepository.findAllById(request.permissionIds());
    role.setPermissions(Set.copyOf(permissions));

    return ResponseEntity.ok(roleRepository.save(role));
  }

  @DeleteMapping("/roles/{id}")
  @Transactional
  public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
    if (!roleRepository.existsById(id))
      return ResponseEntity.notFound().build();
    roleRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  public record RoleCreateRequest(String name, String code, List<Long> permissionIds) {
  }

  public record RoleUpdateRequest(String name, String code, List<Long> permissionIds) {
  }
}
