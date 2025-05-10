package com.usermanagement.permissions_srv.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.usermanagement.permissions_srv.dto.RoleDto;
import com.usermanagement.permissions_srv.model.Role;
import com.usermanagement.permissions_srv.repository.PermissionRepository;
import com.usermanagement.permissions_srv.repository.RoleRepository;

import jakarta.transaction.Transactional;

@Service
public class RoleService {

  private final RoleRepository roleRepo;
  private final PermissionRepository permRepo;
  private final EventGridService eventGridService;

  public RoleService(RoleRepository roleRepo, PermissionRepository permRepo, EventGridService eventGridService) {
    this.roleRepo = roleRepo;
    this.permRepo = permRepo;
    this.eventGridService = eventGridService;
  }

  public List<Role> getAllRoles() {
    return roleRepo.findAll();
  }

  public Optional<Role> getRoleById(Long id) {
    return roleRepo.findById(id);
  }

  @Transactional
  public Role createRole(RoleDto request) {
    System.out.println("===> Permission IDs recibidos: " + request.permissionIds());

    Role role = new Role();
    role.setName(request.name());
    role.setCode(request.code());
    role.setPermissions(new HashSet<>(permRepo.findAllById(request.permissionIds())));
    return roleRepo.save(role);
  }

  @Transactional
  public Role updateRole(Long id, RoleDto request) {
    Role role = roleRepo.findById(id).orElseThrow();
    role.setName(request.name());
    role.setCode(request.code());
    role.setPermissions(new HashSet<>(permRepo.findAllById(request.permissionIds())));
    return roleRepo.save(role);
  }

  @Transactional
  public void deleteRole(Long id) {
    roleRepo.deleteById(id);
    try {
     eventGridService.ExecuteRolDeletedEventFor(id);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el evento rol_deleted");
    }
    
  }
}
