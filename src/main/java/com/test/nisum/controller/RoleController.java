package com.test.nisum.controller;

import com.test.nisum.domain.dto.RoleDto;
import com.test.nisum.domain.entity.Role;
import com.test.nisum.domain.model.ResponseApi;
import com.test.nisum.service.RoleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "${api.path}")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "JWT")
    @PostMapping(value = "${role.path}",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApi<RoleDto>> register(@Valid @RequestBody RoleDto request) {
        ResponseApi<RoleDto> responseApi = new ResponseApi<>();
        Optional<Role> roleOptional = this.roleService.store(request);

        roleOptional.ifPresent(role -> responseApi.setBody(RoleDto.convert(role)));

        return ResponseEntity.status(HttpStatus.CREATED).body(responseApi);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    @SecurityRequirement(name = "JWT")
    @GetMapping(value = "${role.path}/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApi<RoleDto>> getOne(@Valid @PathVariable("name") String roleName) {
        ResponseApi<RoleDto> responseApi = new ResponseApi<>();
        Optional<Role> roleOptional = this.roleService.search(roleName);

        roleOptional.ifPresent(role -> responseApi.setBody(RoleDto.convert(role)));

        return ResponseEntity.status(HttpStatus.OK).body(responseApi);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NORMAL')")
    @SecurityRequirement(name = "JWT")
    @GetMapping(value = "${role.list.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApi<List<RoleDto>>> getAll() {
        ResponseApi<List<RoleDto>> responseApi = new ResponseApi<>();
        List<Role> roles = this.roleService.search();

        responseApi.setBody(roles.stream().map(RoleDto::convert).collect(Collectors.toList()));

        return ResponseEntity.status(HttpStatus.OK).body(responseApi);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping(value = "${role.path}/{name}")
    public ResponseEntity<Object> removeOne(@Valid @PathVariable("name") String roleName) {

        this.roleService.destroy(roleName);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
