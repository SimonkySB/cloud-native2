package com.usermanagement.identity_srv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.usermanagement.identity_srv.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
