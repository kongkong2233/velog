package org.example.velog.service;

import lombok.extern.slf4j.Slf4j;
import org.example.velog.entity.User;
import org.example.velog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        }

        Set<GrantedAuthority> grantedAuthorities = user.get().getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toSet());
        log.info("Granted authorities: {}", grantedAuthorities);
        log.info("Loaded user: {}, Password: {}, Granted authorities: {}",
                user.get().getUsername(), user.get().getPassword(), grantedAuthorities);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.get().getUsername(),
                user.get().getPassword(),
                grantedAuthorities
        );

        log.info("UserDetails: {}", userDetails);
        return userDetails;
    }
}