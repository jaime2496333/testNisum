package com.test.nisum.security.filter;

import com.test.nisum.service.UserDetailsSecurityService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtAuthorizationFilter extends GenericFilterBean {
    private static final String BEARER = "Bearer";

    private final UserDetailsSecurityService userDetailsSecurityService;

    public JwtAuthorizationFilter(UserDetailsSecurityService userDetailsSecurityService) {
        this.userDetailsSecurityService = userDetailsSecurityService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authorizationHeader = ((HttpServletRequest) request).getHeader(AUTHORIZATION);

        if(Objects.nonNull(authorizationHeader)) {

            getBearerToken(authorizationHeader)
                    .flatMap(this.userDetailsSecurityService::loadUserByJwtToken)
                    .ifPresent(userDetails -> {
                        SecurityContextHolder.getContext().setAuthentication(
                                new PreAuthenticatedAuthenticationToken(
                                        userDetails, "", userDetails.getAuthorities())
                        );
            });
        }

        chain.doFilter(request, response);
    }

    private Optional<String> getBearerToken(String authorizationHeader) {
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith(BEARER)) {
            return Optional.of(authorizationHeader.replace(BEARER, "").trim());
        }
        return Optional.empty();
    }
    
}
