package com.test.nisum.controller;

import com.test.nisum.domain.dto.RoleDto;
import com.test.nisum.domain.entity.Role;
import com.test.nisum.domain.model.ResponseApi;
import com.test.nisum.service.RoleService;
import com.test.nisum.exception.UserApiActionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    RoleService roleService;

    @Test
    void register_expectedSuccessful() {
        RoleController roleController = new RoleController(roleService);

        Mockito.when(roleService.store(any(RoleDto.class))).thenReturn(getDummyEntity());
        ResponseEntity<ResponseApi<RoleDto>> response = roleController.register(getDummyRequest());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(getDummyRequest().getName(), Objects.requireNonNull(response.getBody()).getBody().getName());
    }

    @Test
    void register_expectedUserApiActionException() {
        RoleController roleController = new RoleController(roleService);

        Mockito.when(roleService.store(any(RoleDto.class)))
                .thenThrow( new UserApiActionException("not created", HttpStatus.FORBIDDEN));
        UserApiActionException userApiActionException = assertThrows(UserApiActionException.class,
                () -> roleController.register(getDummyRequest())
        );

        assertEquals(HttpStatus.FORBIDDEN, userApiActionException.getStatus());
        assertTrue(userApiActionException.getMessage().contains("not created"));
    }

    @Test
    void getOne_expectedSuccessful() {
        RoleController roleController = new RoleController(roleService);

        Mockito.when(roleService.search(anyString())).thenReturn(getDummyEntity());
        ResponseEntity<ResponseApi<RoleDto>> response = roleController.getOne(getDummyRequest().getName());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getDummyRequest().getName(), Objects.requireNonNull(response.getBody()).getBody().getName());
    }

    @Test
    void getOne_expectedUserApiActionException() {
        RoleController roleController = new RoleController(roleService);

        Mockito.when(roleService.store(any(RoleDto.class)))
                .thenThrow( new UserApiActionException("not found", HttpStatus.NOT_FOUND));
        UserApiActionException userApiActionException = assertThrows(UserApiActionException.class,
                () -> roleController.register(getDummyRequest())
        );

        assertEquals(HttpStatus.NOT_FOUND, userApiActionException.getStatus());
        assertTrue(userApiActionException.getMessage().contains("not found"));
    }

    @Test
    void getAll_expectedSuccessful() {
        RoleController roleController = new RoleController(roleService);

        List<Role> roles = new ArrayList<>();
        getDummyEntity().ifPresent(roles::add);

        Mockito.when(roleService.search()).thenReturn(roles);
        ResponseEntity<ResponseApi<List<RoleDto>>> response = roleController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                getDummyRequest().getName(),
                Objects.requireNonNull(response.getBody()).getBody().get(0).getName()
        );
    }

    @Test
    void removeOne_expectedSuccessful() {
        RoleController roleController = new RoleController(roleService);

        ResponseEntity<Object> response = roleController.removeOne(getDummyRequest().getName());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void removeOne_expectedUserApiActionException() {
        RoleController roleController = new RoleController(roleService);

        Mockito.doThrow(new UserApiActionException("not found", HttpStatus.NOT_FOUND))
                .when(roleService).destroy(anyString());
        UserApiActionException userApiActionException = assertThrows(UserApiActionException.class,
                () -> roleController.removeOne(getDummyRequest().getName())
        );

        assertEquals(HttpStatus.NOT_FOUND, userApiActionException.getStatus());
    }

    private RoleDto getDummyRequest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setName("TEST");
        roleDto.setDescription("Role for testing");
        return roleDto;
    }

    private Optional<Role> getDummyEntity() {
        Role role = new Role();
        role.setId(1L);
        role.setName(getDummyRequest().getName());
        role.setDescription(getDummyRequest().getDescription());
        role.setCreated(new Date());
        role.setModified(role.getCreated());
        role.setCreatedBy("anonymous");
        role.setModifiedBy("anonymous");
        return Optional.of(role);
    }
}