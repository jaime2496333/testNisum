package com.test.nisum.controller;

import com.test.nisum.domain.dto.UserDto;
import com.test.nisum.domain.entity.User;
import com.test.nisum.domain.model.ResponseApi;
import com.test.nisum.domain.model.TokenResponseApi;
import com.test.nisum.service.UserService;
import com.test.nisum.util.CommonUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
public class UserController {

    private final MessageSource messageSource;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @PostMapping(value = "${user.signup.path}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApi<UserDto>> signup(@Valid @RequestBody UserDto request) {
        ResponseApi<UserDto> responseApi = new ResponseApi<>();
        Optional<User> userOptional = this.userService.store(request);

        userOptional.ifPresent(user -> responseApi.setBody(UserDto.convert(user)));
        responseApi.setMessage(CommonUtil.getMessage(this.messageSource, "usuario.logueado.ok"));

        return ResponseEntity.status(HttpStatus.CREATED).body(responseApi);
    }

    @PostMapping(value = "${user.signin.path}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponseApi> signin(@Valid @RequestBody UserDto request) {
        TokenResponseApi responseApi = new TokenResponseApi();
        Optional<String> tokenOptional = this.userService.authenticate(request.getEmail(), request.getPassword());

        tokenOptional.ifPresent(responseApi::setToken);

        return ResponseEntity.status(HttpStatus.OK).body(responseApi);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_NORMAL')")
    @SecurityRequirement(name = "JWT")
    @GetMapping(value = "${user.list.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseApi<List<UserDto>>> getAll() {
        ResponseApi<List<UserDto>> responseApi = new ResponseApi<>();
        List<User> users = this.userService.search();

        responseApi.setBody(users.stream().map(UserDto::convert).collect(Collectors.toList()));

        return ResponseEntity.status(HttpStatus.OK).body(responseApi);
    }

}
