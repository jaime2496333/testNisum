package com.test.nisum.domain.dto;

import com.test.nisum.domain.entity.Role;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RoleDto {

    @NotNull(message = "{value.not.null}")
    @NotEmpty(message = "{value.not.empty}")
    private String name;

    private String description;

    public RoleDto() {
//         Empty
    }

    public RoleDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Role convert(RoleDto roleDto) {
        Role role = new Role();
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        return role;
    }

    public static RoleDto convert(Role role) {
        return new RoleDto(role.getName(), role.getDescription());
    }

    @Override
    public String toString() {
        return "RoleDto{" +
                "name='" + this.name + '\'' +
                ", description='" + this.description + '\'' +
                '}';
    }
}
