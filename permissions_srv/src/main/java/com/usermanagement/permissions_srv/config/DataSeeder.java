package com.usermanagement.permissions_srv.config;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.usermanagement.permissions_srv.model.Permission;
import com.usermanagement.permissions_srv.model.Role;
import com.usermanagement.permissions_srv.repository.PermissionRepository;
import com.usermanagement.permissions_srv.repository.RoleRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataSeeder {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  public DataSeeder(RoleRepository roleRepository, PermissionRepository permissionRepository) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
  }

  @PostConstruct
  @Transactional
  public void seedData() {
    System.out.println("🔍 Permisos en DB: " + permissionRepository.count());
    System.out.println("🔍 Roles en DB: " + roleRepository.count());
    if (permissionRepository.count() == 0) {
      List<Permission> permissions = List.of(
          new Permission("Crear usuario", "PERM_CREATE_USER"),
          new Permission("Editar usuario", "PERM_EDIT_USER"),
          new Permission("Eliminar usuario", "PERM_DELETE_USER"),
          new Permission("Ver usuarios", "PERM_VIEW_USERS"));

      permissionRepository.saveAll(permissions);
      permissionRepository.flush();
    }

    if (roleRepository.count() == 0) {
      Permission view = permissionRepository.findByCode("PERM_VIEW_USERS").orElseThrow();
      Permission create = permissionRepository.findByCode("PERM_CREATE_USER").orElseThrow();
      Permission edit = permissionRepository.findByCode("PERM_EDIT_USER").orElseThrow();
      Permission delete = permissionRepository.findByCode("PERM_DELETE_USER").orElseThrow();

      Role admin = new Role();
      admin.setName("Administrador");
      admin.setCode("ROLE_ADMIN");

      Role user = new Role();
      user.setName("Usuario");
      user.setCode("ROLE_USER");

      roleRepository.save(admin);
      roleRepository.save(user);

      admin.setPermissions(Set.of(view, create, edit, delete));
      user.setPermissions(Set.of(view));

      roleRepository.save(admin);
      roleRepository.save(user);
      roleRepository.flush();
    }
  }
}
