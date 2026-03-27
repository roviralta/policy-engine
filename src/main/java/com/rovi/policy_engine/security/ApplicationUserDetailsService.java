package com.rovi.policy_engine.security;

import com.rovi.policy_engine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String encodedPassword = userRepository.getEncodedPassword(username);
        if (encodedPassword == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return ApplicationUser.create(username, encodedPassword, "USER");
    }
}
