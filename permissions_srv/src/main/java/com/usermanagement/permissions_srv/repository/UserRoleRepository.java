package com.usermanagement.permissions_srv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usermanagement.permissions_srv.model.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
  List<UserRole> findByUserEmail(String userEmail);
}
