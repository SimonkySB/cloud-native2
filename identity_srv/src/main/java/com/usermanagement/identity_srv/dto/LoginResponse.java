package com.usermanagement.identity_srv.dto;

public class LoginResponse {

  private final String token;

  public LoginResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
