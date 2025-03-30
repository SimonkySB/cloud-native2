package com.usermanagement.permissions_srv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usermanagement.permissions_srv.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
  Optional<Permission> findByCode(String code);
}
