package com.test.nisum.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserDetailsSecurityService extends UserDetailsService {
    Optional<UserDetails> loadUserByJwtToken(String jwtToken);
}
