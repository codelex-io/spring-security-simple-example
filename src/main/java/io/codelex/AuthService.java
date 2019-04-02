package io.codelex;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import static java.util.Collections.singleton;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Component
class AuthService {

    void authorise(String email, String role) {
        var authorities = singleton(new SimpleGrantedAuthority(role));
        var token = new UsernamePasswordAuthenticationToken(email, null, authorities);
        getContext().setAuthentication(token);
    }

    void clearAuthentication() {
        getContext().setAuthentication(null);
    }
}
