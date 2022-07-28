package com.test.nisum.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.test.nisum.domain.entity.Phone;
import com.test.nisum.domain.entity.Role;
import com.test.nisum.util.RoleType;
import com.test.nisum.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private UUID id;

    private String name;

    @NotNull(message = "${value.not.null}")
    @NotEmpty(message = "${value.not.empty}")
    @Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", message = "{email.invalid}")
    private String email;

    @NotNull(message = "${value.not.null}")
    @NotEmpty(message = "${value.not.empty}")
    private String password;

    private String token;

    private boolean isActive;

    private LocalDateTime lastLogin;

    private LocalDateTime created;

    private LocalDateTime modified;

    private List<PhoneDto> phones;

    private List<RoleDto> roles;

    public UserDto() {
//        Empty
    }

    public UserDto(String name, String email, String password, List<PhoneDto> phones) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phones = phones;
    }

    public UserDto(UUID id, String name, String email, String password, String token, boolean active,
                   LocalDateTime lastLogin, LocalDateTime created, LocalDateTime modified, List<PhoneDto> phones,
                   List<RoleDto> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = token;
        this.isActive = active;
        this.lastLogin = lastLogin;
        this.created = created;
        this.modified = modified;
        this.phones = phones;
        this.roles = roles;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public LocalDateTime getLastLogin() {
        return this.lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return this.modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public List<PhoneDto> getPhones() {
        return this.phones;
    }

    public void setPhones(List<PhoneDto> phones) {
        this.phones = phones;
    }

    public List<RoleDto> getRoles() {
        return this.roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }

    public static User convert(UserDto userDto, String passwordEncoded, String token, Role role) {
        final User user = new User();

        if(Objects.isNull(userDto) || Objects.isNull(passwordEncoded)
                || Objects.isNull(token) || Objects.isNull(role)) {
            throw new IllegalArgumentException("arg.invalidos");
        }

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoded);
        user.setToken(token);
        user.setActive(true);
        user.setRoles(new HashSet<>(Collections.singletonList(role)));

        List<Phone> phones = userDto.getPhones().stream().map(phoneDto -> {
            Phone phone = new Phone();
            phone.setNumber(phoneDto.getNumber());
            phone.setCityCode(phoneDto.getCityCode());
            phone.setCountryCode(phoneDto.getCountryCode());
            phone.setUser(user);
            return phone;
        }).collect(Collectors.toList());

        user.setPhones(new HashSet<>(phones));

        return user;
    }

    public static UserDto convert(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setToken(user.getToken());
        userDto.setActive(user.getActive());
        userDto.setCreated(user.getCreated()
                .toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        userDto.setModified(user.getModified()
                .toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        userDto.setLastLogin(user.getLastLogin()
                .toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime());

        if(authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority ->  grantedAuthority.getAuthority().equals(RoleType.ROLE_ADMIN.name()))){
            userDto.setPhones(user.getPhones().stream().map(
                    phone -> new PhoneDto(
                            phone.getNumber(),
                            phone.getCityCode(),
                            phone.getCountryCode()
                    )
            ).collect(Collectors.toList()));

            userDto.setRoles(user.getRoles().stream().map(
                    role -> new RoleDto(
                            role.getName(),
                            role.getDescription()
                    )
            ).collect(Collectors.toList()));
        }

        return userDto;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", email='" + this.email + '\'' +
                ", password='" + this.password + '\'' +
                ", token='" + this.token + '\'' +
                ", active=" + this.isActive +
                ", lastLogin=" + this.lastLogin +
                ", created=" + this.created +
                ", modified=" + this.modified +
                ", phones=" + this.phones +
                ", roles=" + this.roles +
                '}';
    }
}
