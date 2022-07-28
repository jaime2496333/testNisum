package com.test.nisum.exception;

import com.test.nisum.domain.model.ErrorApi;
import com.test.nisum.domain.model.ResponseApi;
import com.test.nisum.util.CommonUtil;
import io.jsonwebtoken.security.SignatureException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.AbstractMap;
import java.util.stream.Collectors;

@RestControllerAdvice
public class HandlerController {

    private final MessageSource messageSource;
    private final String codeErrorPrefix;

    @Autowired
    public HandlerController(@Value("${user.api.exception.prefix}") String errorPrefix, MessageSource messageSource) {
        this.messageSource = messageSource;
        this.codeErrorPrefix = errorPrefix;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseApi<Object>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(this.buildResponse(
                        this.getMessage("permiso.denegado"), e.getMessage(), e));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ResponseApi<Object>> handleSignature(SignatureException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(this.buildResponse(
                        this.getMessage("credenciales.invalidas"), e.getMessage(), e));
    }

    @ExceptionHandler(AttemptAuthenticationException.class)
    public ResponseEntity<ResponseApi<Object>> handleJwtAttemptAuthentication(AttemptAuthenticationException e) {
        return ResponseEntity.status(e.getStatus())
                .body(this.buildResponse(
                        this.getMessage("error.desconocido"),
                        this.getMessage(e.getMessage()),
                        e.getStatus(), e)
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseApi<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(this.buildResponse(e));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseApi<Object>> handleUsernameNotFound(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(this.buildResponse(this.getMessage(e.getMessage())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseApi<Object>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(this.buildResponse(
                        this.getMessage("error.desconocido"),
                        this.getMessage( e.getMessage()), e));
    }

    @ExceptionHandler(UserApiActionException.class)
    public ResponseEntity<ResponseApi<Object>> handleUserApiAction(UserApiActionException e) {
        return ResponseEntity.status(e.getStatus())
                .body(this.buildResponse(this.getMessage(e.getMessage())));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseApi<Object>> handleConstraintViolation(ConstraintViolationException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        return ResponseEntity.status(status).body(
                this.buildResponse(this.getMessage("error.desconocido"), e.getMessage(), status, e));
    }

    private ResponseApi<Object> buildResponse(String message) {
        ResponseApi<Object> response = new ResponseApi<>();
        response.setMessage(message);

        return response;
    }

    private ResponseApi<Object> buildResponse(String title, String message, Throwable e) {
        ErrorApi error = new ErrorApi(title, message, e.getClass().getName());

        ResponseApi<Object> response = new ResponseApi<>();
        response.setError(error);
        response.setMessage(message);

        return response;
    }

    private ResponseApi<Object> buildResponse(String title, String message, HttpStatus status, Throwable e) {
        ErrorApi error = new ErrorApi();
        error.setTitle(title);
        error.setDetail(message);
        error.setStatus(status);
        error.setType(e.getClass().getName());
        error.setCode(this.codeErrorPrefix + status.value());

        ResponseApi<Object> response = new ResponseApi<>();
        response.setError(error);
        response.setMessage(message);

        return response;
    }

    private ResponseApi<Object> buildResponse(MethodArgumentNotValidException e) {

        ResponseApi<Object> response = new ResponseApi<>();
        response.setMessage(e.getBindingResult().getAllErrors()
                .stream().map(
                        error -> new AbstractMap.SimpleEntry<>(
                                ((FieldError) error).getField(),
                                error.getDefaultMessage()
                        )
                ).collect(Collectors.toList()).toString()
        );

        return response;
    }

    private String getMessage(String messageKey, Object... arguments){
        return CommonUtil.getMessage(this.messageSource, messageKey, arguments);
    }
}
