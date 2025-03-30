package com.usermanagement.permissions_srv.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.usermanagement.permissions_srv.model.Permission;
import com.usermanagement.permissions_srv.repository.PermissionRepository;

import jakarta.transaction.Transactional;

@Service
public class PermissionService {

  private final PermissionRepository permissionRepository;

  public PermissionService(PermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }

  public List<Permission> getAllPermissions() {
    return permissionRepository.findAll();
  }

  public Optional<Permission> getPermissionById(Long id) {
    return permissionRepository.findById(id);
  }

  @Transactional
  public Permission createPermission(Permission permission) {
    return permissionRepository.save(permission);
  }

  @Transactional
  public Permission updatePermission(Long id, Permission request) {
    Permission perm = permissionRepository.findById(id).orElseThrow();
    perm.setName(request.getName());
    perm.setCode(request.getCode());
    return permissionRepository.save(perm);
  }

  @Transactional
  public void deletePermission(Long id) {
    permissionRepository.deleteById(id);
  }
}
