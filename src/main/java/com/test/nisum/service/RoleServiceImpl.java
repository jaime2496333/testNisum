package com.test.nisum.service;

import com.test.nisum.domain.dto.RoleDto;
import com.test.nisum.domain.entity.Role;
import com.test.nisum.exception.UserApiActionException;
import com.test.nisum.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Override
    public Optional<Role> store(RoleDto roleDto) {
        Optional<Role> roleOptional = this.roleRepository.findByName(roleDto.getName());

        roleOptional.ifPresent(role -> { throw new UserApiActionException("rol.existe", HttpStatus.FORBIDDEN);});

        return Optional.of(this.roleRepository.save(RoleDto.convert(roleDto)));
    }

    @Override
    public Optional<Role> search(String roleName) {
        return Optional.ofNullable(this.roleRepository.findByName(roleName).orElseThrow(
                () -> new UserApiActionException("rol.no.existe", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<Role> search() {
        List<Role> roles = new ArrayList<>();
        this.roleRepository.findAll().forEach(roles::add);
        return roles;
    }

    @Transactional
    @Override
    public void destroy(String roleName) {
        Optional<Role> roleOptional = this.search(roleName);
        roleOptional.ifPresent( role -> this.roleRepository.delete(roleOptional.get()));
    }
}
