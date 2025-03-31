package com.usermanagement.permissions_srv.controller;

import java.util.List;

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
import com.usermanagement.permissions_srv.service.PermissionService;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

  private final PermissionService permissionService;

  public PermissionController(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @GetMapping
  public ResponseEntity<List<Permission>> getAll() {
    return ResponseEntity.ok(permissionService.getAllPermissions());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Permission> getById(@PathVariable Long id) {
    return permissionService.getPermissionById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Permission> create(@RequestBody Permission permission) {
    return ResponseEntity.ok(permissionService.createPermission(permission));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Permission> update(@PathVariable Long id, @RequestBody Permission permission) {
    return ResponseEntity.ok(permissionService.updatePermission(id, permission));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    permissionService.deletePermission(id);
    return ResponseEntity.noContent().build();
  }
}
