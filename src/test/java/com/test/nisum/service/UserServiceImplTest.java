package com.test.nisum.service;

import com.test.nisum.domain.dto.UserDto;
import com.test.nisum.domain.entity.Phone;
import com.test.nisum.domain.entity.Role;
import com.test.nisum.domain.entity.User;
import com.test.nisum.exception.AttemptAuthenticationException;
import com.test.nisum.repository.RoleRepository;
import com.test.nisum.security.provider.JwtProvider;
import com.test.nisum.domain.dto.PhoneDto;
import com.test.nisum.exception.UserApiActionException;
import com.test.nisum.repository.PhoneRepository;
import com.test.nisum.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlZGdld2wyQGdtYWlsLmNvbSIsInJvbGVzIjpbey" +
            "JhdXRob3JpdHkiOiJST0xFX0FETUlOIn1dLCJpYXQiOjE2NDM2OTg4MzIsImV4cCI6MTY0NjI5MDgzMn0.nqGpGPdntzShNhVOkG" +
            "-dzjTydxU66im_L2XP7hIBN-g";

    private static final String ENCODED_PASSWORD = "$2b$10$6MKZAp7DeZTqSbIHqdXBKONcObiysknAiExUWjOtls91AmHOLvoNS";

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PhoneRepository phoneRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtProvider jwtProvider;

    @Test
    void store_whenUserIsNew_shouldReturnOptionalUser() {
        UserService userService = new UserServiceImpl(userRepository, roleRepository, phoneRepository,
                passwordEncoder, authenticationManager, jwtProvider);

        Mockito.when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Mockito.when(this.phoneRepository.findByNumber(anyString())).thenReturn(Optional.empty());
        Mockito.when(this.roleRepository.findByName(anyString())).thenReturn(getDummyRoleEntity());
        Mockito.when(this.jwtProvider.generateToken(anyString(), anyList())).thenReturn(TOKEN);
        Mockito.when(this.passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        Mockito.when(this.userRepository.save(any(User.class))).thenReturn(getDummyUserEntity());

        Optional<User> userOptional = userService.store(getDummyRequest());

        Assertions.assertEquals(getDummyRequest().getEmail(), userOptional.get().getEmail());
        assertTrue(userOptional.get().getActive());
    }

    @Test
    void store_whenUserExist_shouldThrowAnException_emailFound() {
        UserService userService = new UserServiceImpl(userRepository, roleRepository, phoneRepository,
                passwordEncoder, authenticationManager, jwtProvider);

        Mockito.when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(getDummyUserEntity()));
        UserApiActionException userApiActionException = assertThrows(UserApiActionException.class,
                () -> userService.store(getDummyRequest())
        );

        assertTrue(userApiActionException.getMessage().contains("usuario.existe"));
    }

    @Test
    void store_whenPhoneExist_shouldThrowAnException_phoneFound() {
        UserService userService = new UserServiceImpl(userRepository, roleRepository, phoneRepository,
                passwordEncoder, authenticationManager, jwtProvider);

        Mockito.when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Mockito.when(this.phoneRepository.findByNumber(anyString())).thenReturn(getDummyPhoneEntity());
        UserApiActionException userApiActionException = assertThrows(UserApiActionException.class,
                () -> userService.store(getDummyRequest())
        );

        assertTrue(userApiActionException.getMessage().contains("tel.existe"));
    }

    @Test
    void search_whenUserIsFound_shouldReturnOptionalUser() {
        UserService userService = new UserServiceImpl(userRepository, roleRepository, phoneRepository,
                passwordEncoder, authenticationManager, jwtProvider);

        Mockito.when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(getDummyUserEntity()));

        Optional<User> userOptional = userService.search(getDummyRequest().getEmail());

        Assertions.assertEquals(getDummyRequest().getEmail(), userOptional.get().getEmail());
        assertTrue(userOptional.get().getActive());
    }

    @Test
    void search_whenUserNotExist_shouldThrowAnException_userNotFound() {
        UserService userService = new UserServiceImpl(userRepository, roleRepository, phoneRepository,
                passwordEncoder, authenticationManager, jwtProvider);

        Mockito.when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        UserApiActionException userApiActionException = assertThrows(UserApiActionException.class,
                () -> userService.search(getDummyRequest().getEmail())
        );

        assertTrue(userApiActionException.getMessage().contains("usuario.no.existe"));
    }

    @Test
    void authenticate_whenUserExist_shouldReturnToken() {
        UserService userService = new UserServiceImpl(userRepository, roleRepository, phoneRepository,
                passwordEncoder, authenticationManager, jwtProvider);

        Mockito.when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(getDummyUserEntity()));
        Mockito.when(this.jwtProvider.generateToken(anyString(), anyList())).thenReturn(TOKEN);
        Mockito.when(this.userRepository.save(any(User.class))).thenReturn(getDummyUserEntity());

        Optional<String> tokenOptional = userService.authenticate(
                getDummyRequest().getEmail(), getDummyRequest().getPassword());

        assertEquals(TOKEN, tokenOptional.get());
    }

    @Test
    void authenticate_whenUserNotExist_shouldThrowAnException_userNotFound() {
        UserService userService = new UserServiceImpl(userRepository, roleRepository, phoneRepository,
                passwordEncoder, authenticationManager, jwtProvider);

        Mockito.when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        UserApiActionException userApiActionException = assertThrows(UserApiActionException.class,
                () -> userService.authenticate(
                        getDummyRequest().getEmail(), getDummyRequest().getPassword())
        );

        assertTrue(userApiActionException.getMessage().contains("email.no.existe"));
    }

    @Test
    void authenticate_whenUserNotExist_shouldThrowAnException_unauthorized() {
        UserService userService = new UserServiceImpl(userRepository, roleRepository, phoneRepository,
                passwordEncoder, authenticationManager, jwtProvider);

        Mockito.when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(getDummyUserEntity()));
        Mockito.when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(AttemptAuthenticationException.class);
        AttemptAuthenticationException jwtAttemptAuthenticationException = assertThrows(AttemptAuthenticationException.class,
                () -> userService.authenticate(
                        getDummyRequest().getEmail(), getDummyRequest().getPassword())
        );

        assertTrue(jwtAttemptAuthenticationException.getMessage().contains("usuario.logueado.error"));
        assertEquals(HttpStatus.UNAUTHORIZED, jwtAttemptAuthenticationException.getStatus());
    }

    @Test
    void search_whenGetAll_shouldReturnUserList() {
        Iterable<User> users = Collections.singletonList(getDummyUserEntity());
        UserService userService = new UserServiceImpl(userRepository, roleRepository, phoneRepository,
                passwordEncoder, authenticationManager, jwtProvider);

        Mockito.when(this.userRepository.findAll()).thenReturn(users);
        List<User> userList = userService.search();

        Assertions.assertEquals(getDummyRequest().getEmail(), userList.get(0).getEmail());
        assertTrue(userList.get(0).getActive());
    }

    private UserDto getDummyRequest() {
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setNumber(getDummyPhoneEntity().get().getNumber());
        phoneDto.setCityCode(getDummyPhoneEntity().get().getCityCode());
        phoneDto.setCountryCode(getDummyPhoneEntity().get().getCountryCode());

        UserDto userDto = new UserDto();
        userDto.setName("Fulanito Sutanito");
        userDto.setEmail("fulisut@test.com");
        userDto.setPassword("159piyrw%#*357");
        userDto.setPhones(Collections.singletonList(phoneDto));

        return userDto;
    }

    private Optional<Phone> getDummyPhoneEntity() {
        Phone phone = new Phone();
        phone.setId(1L);
        phone.setNumber("87654321");
        phone.setCityCode("3200");
        phone.setCountryCode("505");
        phone.setCreated(new Date());
        phone.setModified(new Date());
        phone.setCreatedBy("anonymous");
        phone.setModifiedBy("anonymous");

        return Optional.of(phone);
    }

    private Optional<Role> getDummyRoleEntity() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_NORMAL");
        role.setDescription("Role normal for testing");
        role.setCreated(new Date());
        role.setModified(new Date());
        role.setCreatedBy("anonymous");
        role.setModifiedBy("anonymous");
        return Optional.of(role);
    }

    private User getDummyUserEntity() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setActive(true);
        user.setToken(TOKEN);
        user.setPassword(ENCODED_PASSWORD);
        user.setName(getDummyRequest().getName());
        user.setEmail(getDummyRequest().getEmail());
        user.setLastLogin(new Date());
        user.setCreated(user.getLastLogin());
        user.setModified(user.getLastLogin());
        user.setCreatedBy("anonymous");
        user.setModifiedBy("anonymous");
        user.setRoles(new HashSet<>(Collections.singletonList(getDummyRoleEntity().get())));
        user.setPhones(new HashSet<>(Collections.singletonList(getDummyPhoneEntity().get())));

        return user;
    }
}