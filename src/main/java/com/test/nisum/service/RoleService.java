package com.test.nisum.service;

import com.test.nisum.domain.dto.RoleDto;
import com.test.nisum.domain.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    Optional<Role> store(RoleDto roleDto);

    Optional<Role> search(String roleName);

    List<Role> search();

    void destroy(String roleName);
}
