package com.test.nisum.service;

import com.test.nisum.domain.dto.UserDto;
import com.test.nisum.domain.entity.Phone;
import com.test.nisum.domain.entity.Role;
import com.test.nisum.domain.entity.User;
import com.test.nisum.exception.AttemptAuthenticationException;
import com.test.nisum.exception.UserApiActionException;
import com.test.nisum.repository.PhoneRepository;
import com.test.nisum.repository.RoleRepository;
import com.test.nisum.repository.UserRepository;
import com.test.nisum.security.provider.JwtProvider;
import com.test.nisum.util.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PhoneRepository phoneRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PhoneRepository phoneRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.phoneRepository = phoneRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    @Override
    public Optional<User> store(UserDto userDto) {
        Optional<User> userSaved = Optional.empty();
        Optional<User> userExists = this.userRepository.findByEmail(userDto.getEmail());

        userExists.ifPresent(action -> {
            throw new UserApiActionException("usuario.existe", HttpStatus.FORBIDDEN);
        });

        if(!userDto.getPhones().isEmpty()){
            List<Optional<Phone>> phoneOptionals = userDto.getPhones().stream()
                    .map(phone -> this.phoneRepository.findByNumber(phone.getNumber()))
                    .collect(Collectors.toList());

            phoneOptionals.forEach(
                    phoneOptional -> phoneOptional.ifPresent(
                            phone -> { throw new UserApiActionException("tel.existe", HttpStatus.FORBIDDEN); }
                    )
            );
        }

        Optional<Role> role = this.roleRepository.findByName(RoleType.ROLE_NORMAL.name());
        if (role.isPresent()) {
            userDto.setToken(this.jwtProvider.generateToken(userDto.getEmail(), Collections.singletonList(role.get())));
            userSaved = Optional.of(
                    this.userRepository.save(
                            UserDto.convert(userDto, this.passwordEncoder.encode(userDto.getPassword()),
                                    userDto.getToken(), role.get())
                    )
            );
        }
        return userSaved;
    }

    @Override
    public Optional<User> search(String email) {
        User user = this.userRepository.findByEmail(email).orElseThrow(() ->
                new UserApiActionException("usuario.no.existe", HttpStatus.NOT_FOUND));

        return Optional.of(user);
    }

    @Transactional
    @Override
    public Optional<String> authenticate(String email, String password) {
        Optional<String> token = Optional.empty();
        Optional<User> userOptional = Optional.ofNullable(this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserApiActionException("email.no.existe", HttpStatus.NOT_FOUND)));

        if(userOptional.isPresent()) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
                token = Optional.of(this.jwtProvider.generateToken(email, new ArrayList<>(userOptional.get().getRoles())));
                userOptional.get().setToken(token.get());
                userOptional.get().setActive(true);
                this.userRepository.save(userOptional.get());
            } catch (AuthenticationException e){
                throw new AttemptAuthenticationException("usuario.logueado.error", e, HttpStatus.UNAUTHORIZED);
            }
        }
        return token;
    }

    @Override
    public List<User> search() {
        List<User> users = new ArrayList<>();
        this.userRepository.findAll().forEach(users::add);
        return users;
    }
}
