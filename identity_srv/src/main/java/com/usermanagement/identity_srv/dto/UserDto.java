package com.usermanagement.identity_srv.dto;

import java.util.List;

public record UserDto(
    String email,
    String username,
    String password,
    boolean active,
    List<Long> roleIds) {
}
