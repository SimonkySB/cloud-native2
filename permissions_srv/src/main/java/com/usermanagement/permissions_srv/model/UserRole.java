package com.usermanagement.permissions_srv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_roles")
public class UserRole {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String userEmail;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  public Long getId() {
    return id;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public Role getRole() {
    return role;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
