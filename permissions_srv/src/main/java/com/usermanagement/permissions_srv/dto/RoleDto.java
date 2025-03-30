package com.usermanagement.permissions_srv.dto;

import java.util.List;

public record RoleDto(String name, String code, List<Long> permissionIds) {
}
