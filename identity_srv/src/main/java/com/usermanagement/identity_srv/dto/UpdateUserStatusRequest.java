package com.usermanagement.identity_srv.dto;

public class UpdateUserStatusRequest {
  private boolean active;

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
