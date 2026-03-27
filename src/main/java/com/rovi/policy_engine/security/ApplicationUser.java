package com.rovi.policy_engine.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class ApplicationUser implements UserDetails {

    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public static ApplicationUser create(String username, String password, String role) {
        return new ApplicationUser(
                username,
                password,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
